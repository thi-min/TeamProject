import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { api } from "../../../common/api/axios.js";
import '../style/Animal.css';

const AnimalForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { id } = useParams();

    const [animals, setAnimals] = useState([]);
    const [animalDetail, setAnimalDetail] = useState(null);
    const [message, setMessage] = useState(null);

    // JWT 토큰에서 역할 및 회원 번호 정보 추출
    const getInfoFromToken = () => {
        const token = localStorage.getItem('accessToken');
        if (!token) return null;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return {
                role: payload.role,
                memberNum: payload.memberNum
            };
        } catch (e) {
            console.error("JWT 토큰 파싱 실패:", e);
            return null;
        }
    };
    const userInfo = getInfoFromToken();
    const isAdmin = userInfo?.role === 'ADMIN';

    // 현재 URL 경로에 따른 모드 판단
    const isListView = location.pathname === '/admin/animal/list' || location.pathname === '/animal/list';
    const isDetailView = location.pathname.startsWith('/admin/animal/detail/') || location.pathname.startsWith('/animal/detail/');
    const isCreateView = location.pathname.startsWith('/admin/animal/regist');
    const isUpdateView = location.pathname.startsWith('/admin/animal/update/');

    // 폼 입력 상태
    const [userInput, setUserInput] = useState({
        animalName: '',
        animalBreed: '',
        animalSex: '',
        animalDate: '',
        animalContent: '',
        animalState: '',
        adoptDate: '',
    });

    // 데이터 로딩 함수
    const fetchAnimals = async () => {
        try {
            const response = await api.get('/animals/list');
            setAnimals(response.data.content);
        } catch (error) {
            console.error("목록 조회 실패:", error);
            setMessage("목록을 불러올 수 없습니다.");
        }
    };

    const fetchAnimalDetail = async () => {
        if (!id) return;
        try {
            const response = await api.get(`/animals/detail/${id}`);
            const data = response.data;
            setAnimalDetail(data);
            if (isUpdateView && isAdmin) {
                setUserInput({
                    animalName: data.animalName,
                    animalBreed: data.animalBreed,
                    animalSex: data.animalSex,
                    animalDate: data.animalDate,
                    animalContent: data.animalContent,
                    animalState: data.animalState,
                    adoptDate: data.adoptDate,
                });
            }
        } catch (error) {
            console.error("상세 정보 조회 실패:", error);
            setMessage("상세 정보를 불러올 수 없습니다.");
        }
    };

    // '동물 입양 상담' 버튼 클릭 핸들러 (추가)
    const handleStartChat = async () => {
        const memberNum = userInfo?.memberNum;

        if (!memberNum) {
            alert("로그인 후 이용 가능합니다.");
            navigate('/login');
            return;
        }

        try {
            const response = await api.post(
                '/chat/start-adoption-chat',
                null,
                {
                    params: {
                        memberNum: memberNum,
                        animalId: animalDetail.animalId
                    },
                    headers: {
                        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
                    }
                }
            );

            const chatRoomId = response.data.chatRoomId;
            const destinationPath = isAdmin ? `/admin/chat/room/${chatRoomId}` : `/chat/room/${chatRoomId}`;
            navigate(destinationPath);
        } catch (error) {
            console.error("채팅방 연결 실패:", error);
            alert("채팅 연결에 실패했습니다. 잠시 후 다시 시도해주세요.");
        }
    };

    // 폼 입력 변경 핸들러
    const handleChange = (e) => {
        const { name, value } = e.target;
        setUserInput(prev => ({ ...prev, [name]: value }));
    };

    // 폼 제출 핸들러 (관리자 전용)
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!isAdmin) {
            setMessage("권한이 없습니다.");
            return;
        }
        try {
            if (isCreateView) {
                await api.post('/animals/regist', userInput, {
                    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
                });
                alert("동물 정보가 성공적으로 등록되었습니다.");
            } else if (isUpdateView) {
                await api.put(`/animals/detail/${id}`, userInput, {
                    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
                });
                alert("동물 정보가 성공적으로 수정되었습니다.");
            }
            navigate('/admin/animal/list');
        } catch (error) {
            console.error('제출/수정 실패:', error);
            setMessage('제출/수정에 실패했습니다. 권한을 확인해주세요.');
        }
    };

    // 삭제 핸들러 (관리자 전용)
    const handleDelete = async () => {
        if (!isAdmin) {
            setMessage("권한이 없습니다.");
            return;
        }
        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await api.delete(`/animals/detail/${id}`, {
                    headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
                });
                alert("삭제가 완료되었습니다.");
                navigate('/admin/animal/list');
            } catch (error) {
                console.error("삭제 실패:", error);
                setMessage("삭제에 실패했습니다. 권한을 확인해주세요.");
            }
        }
    };

    // 데이터 로딩 로직
    useEffect(() => {
        if (isListView) {
            fetchAnimals();
        } else if (isDetailView || isUpdateView) {
            fetchAnimalDetail();
        }
    }, [location.pathname, id]);

    if (message) {
        return <div className="loading-message">{message}</div>;
    }

    // ------------------ JSX 렌더링 부분 (레이아웃 변경) ------------------
    return (
        <div>
            <div>

                <h3>{isAdmin ? "동물 정보 관리" : "입양 가능한 동물"}</h3>
            </div>

            {isListView && (
                <div className="form_wrap">

                    <table className="table type2 responsive border">
                        <thead>
                            <tr>
                                <th>이름</th>
                                <th>견종</th>
                                <th>성별</th>
                                <th>상태</th>
                            </tr>
                        </thead>
                        <tbody className="text_center">
                            {animals.length > 0 ? (
                                animals.map((animal) => (
                                    <tr key={animal.animalId}
                                        onClick={() => navigate(isAdmin ? `/admin/animal/detail/${animal.animalId}` : `/animal/detail/${animal.animalId}`)}
                                        style={{ cursor: 'pointer' }}
                                    >
                                        <td>{animal.animalName}</td>
                                        <td>{animal.animalBreed}</td>
                                        <td>{animal.animalSex}</td>
                                        <td>{animal.animalState}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="4">등록된 동물이 없습니다.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                    {isAdmin && (
                        <div className="form_center_box">
                            <div className="temp_btn md">
                                <button onClick={() => navigate('/admin/animal/regist')} className="btn">
                                    동물 정보 등록
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            )}

            {(isCreateView || isUpdateView) && (

                <div>
                    <h3>{isCreateView ? "동물 정보 등록" : "동물 정보 수정"}</h3>

                    <form onSubmit={handleSubmit}>
                        <div className="form_wrap">
                            <table className="table type2 responsive border">
                                <colgroup>
                                    <col className="w20p" />
                                    <col />
                                </colgroup>
                                <tbody>
                                    <tr>
                                        <th scope="row">이름</th>
                                        <td>
                                            <div className="temp_form md w40p">
                                                <input type="text" className="temp_input" name="animalName" value={userInput.animalName} onChange={handleChange} required />
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th scope="row">견종</th>
                                        <td>
                                            <div className="temp_form md w40p">
                                                <input type="text" className="temp_input" name="animalBreed" value={userInput.animalBreed} onChange={handleChange} required />
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th scope="row">성별</th>
                                        <td>
                                            <div className="temp_form_box">
                                                <select className="temp_select" name="animalSex" value={userInput.animalSex} onChange={handleChange} required>
                                                    <option value="">성별 선택</option>
                                                    <option value="MALE">수컷</option>
                                                    <option value="FEMALE">암컷</option>
                                                </select>
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>

                                        <th scope="row">입소일</th>
                                        <td>
                                            <div className="temp_form md w40p">
                                                <input type="date" className="temp_input" name="animalDate" value={userInput.animalDate} onChange={handleChange} required />
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>

                                        <th scope="row">특이사항</th>
                                        <td>
                                            <div className="temp_form md w40p">
                                                <textarea className="temp_input" name="animalContent" value={userInput.animalContent} onChange={handleChange} required />
                                            </div>
                                        </td>
                                    </tr>
                                    <tr>

                                        <th scope="row">상태</th>
                                        <td>
                                            <div className="temp_form_box">
                                                <select className="temp_select" name="animalState" value={userInput.animalState} onChange={handleChange} required>
                                                    <option value="">상태 선택</option>
                                                    <option value="WAIT">보호 중</option>
                                                    <option value="DONE">입양 완료</option>
                                                </select>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div className="form_center_box">
                            <div className="temp_btn white md">
                                <button type="button" className="btn" onClick={() => navigate(-1)}>
                                    이전
                                </button>
                            </div>
                            <div className="temp_btn md">
                                <button type="submit" className="btn">{isCreateView ? "등록" : "수정"}</button>
                            </div>

                        </div>
                    </form>
                </div>
            )}

            {isDetailView && animalDetail && (
                <div className="form_wrap">

                    <h3>동물 상세 정보</h3>

                    <table className="table type2 responsive border">
                        <colgroup>
                            <col className="w20p" />
                            <col />
                        </colgroup>
                        <tbody>
                            <tr>
                                <th scope="row">이름</th>
                                <div className="form_desc">{animalDetail.animalName}</div>
                            </tr>
                            <tr>
                                <th scope="row">견종</th>
                                <div className="form_desc">{animalDetail.animalBreed}</div>
                            </tr>
                            <tr>
                                <th scope="row">성별</th>
                                <div className="form_desc">{animalDetail.animalSex}</div>
                            </tr>
                            <tr>
                                <th scope="row">입소일</th>
                                <div className="form_desc">{animalDetail.animalDate}</div>
                            </tr>
                            <tr>
                                <th scope="row">특이사항</th>
                                <div className="form_desc">{animalDetail.animalContent}</div>
                            </tr>
                            <tr>
                                <th scope="row">상태</th>
                                <div className="form_desc">{animalDetail.animalState}</div>
                            </tr>
                        </tbody>
                    </table>
                    <div className="form_center_box">
                        {/* 일반 사용자에게만 보임 */}
                        {!isAdmin && (
                            <div className="temp_btn md">
                                <button onClick={handleStartChat} className="btn">동물 입양 상담</button>
                            </div>
                        )}
                        <div className="temp_btn white md">
                            <button onClick={() => navigate(-1)} className="btn">이전</button>
                        </div>
                        {isAdmin && (
                            <>
                                <div className="temp_btn md">
                                    <button onClick={() => navigate(`/admin/animal/update/${animalDetail.animalId}`)} className="btn">수정</button>
                                </div>
                                <div className="temp_btn md">
                                    <button onClick={handleDelete} className="btn">삭제</button>
                                </div>
                            </>
                        )}

                    </div>
                </div>
            )}

        </div>

    );
};

export default AnimalForm;