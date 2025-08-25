// import axios from 'axios';
import { api } from "../../../common/api/axios.js";
import { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
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

    // API 요청을 위한 Axios 설정
    const authAxios = api.create({
        baseURL: 'http://localhost:3000/api',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
    });

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
            const response = await api.get('/api/animals');
            setAnimals(response.data);
        } catch (error) {
            console.error("목록 조회 실패:", error);
            setMessage("목록을 불러올 수 없습니다.");
        }
    };

    const fetchAnimalDetail = async () => {
        if (!id) return;
        try {
            const response = await api.get(`/api/animals/${id}`);
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
            // ⭐ 백엔드 API 주소 확인
            const response = await api.post(
                'http://localhost:3000/api/chat/start-adoption-chat', 
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
            // ⭐ 사용자는 '/chat/room/:chatRoomId' 경로로 이동하도록 수정
            // 관리자는 '/admin/chat/room/:chatRoomId' 로 이동
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
                await authAxios.post('/animals', userInput);
                alert("동물 정보가 성공적으로 등록되었습니다.");
            } else if (isUpdateView) {
                await authAxios.put(`/animals/${id}`, userInput);
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
                await authAxios.delete(`/animals/${id}`);
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

    // ------------------ JSX 렌더링 부분 ------------------

    if (isListView) {
        // ... (기존 목록 렌더링) ...
        return (
            <div className="animal-list-page">
                <div className="animal-list-container">
                    <h2 className="animal-list-title">{isAdmin ? "동물 정보 관리" : "입양 가능한 동물"}</h2>
                    {isAdmin && (
                        <div className="button-container">
                            <button onClick={() => navigate('/admin/animal/regist')} className="btn-create-animal">
                                동물 정보 등록
                            </button>
                        </div>
                    )}
                    
                    <table className="animal-table">
                        <thead>
                            <tr>
                                <th>이름</th>
                                <th>견종</th>
                                <th>성별</th>
                                <th>상태</th>
                            </tr>
                        </thead>
                        <tbody>
                            {animals.map((animal) => (
                                <tr key={animal.animalId} 
                                    onClick={() => navigate(isAdmin ? `/admin/animal/detail/${animal.animalId}` : `/animal/detail/${animal.animalId}`)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <td>{animal.animalName}</td>
                                    <td>{animal.animalBreed}</td>
                                    <td>{animal.animalSex}</td>
                                    <td>{animal.animalState}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    } else if (isCreateView || isUpdateView) {
        // ... (기존 등록/수정 렌더링) ...
        if (!isAdmin) {
            return <div>권한이 없습니다.</div>;
        }
        return (
            <div className="animal-form-page">
                <h2>{isCreateView ? "동물 정보 등록" : "동물 정보 수정"}</h2>
                <form onSubmit={handleSubmit}>
                    <input type="text" name="animalName" value={userInput.animalName} onChange={handleChange} placeholder="이름" required />
                    <input type="text" name="animalBreed" value={userInput.animalBreed} onChange={handleChange} placeholder="견종" required />
                    <select name="animalSex" value={userInput.animalSex} onChange={handleChange} required>
                        <option value="">성별 선택</option>
                        <option value="MALE">수컷</option>
                        <option value="FEMALE">암컷</option>
                    </select>
                    <input type="date" name="animalDate" value={userInput.animalDate} onChange={handleChange} placeholder="입소일" required />
                    <textarea name="animalContent" value={userInput.animalContent} onChange={handleChange} placeholder="특이사항" required />
                    <select name="animalState" value={userInput.animalState} onChange={handleChange} required>
                        <option value="">상태 선택</option>
                        <option value="PROTECTING">보호 중</option>
                        <option value="ADOPTED">입양 완료</option>
                    </select>
                    <button type="submit">{isCreateView ? "등록" : "수정"}</button>
                    <button type="button" onClick={() => navigate(-1)}>이전</button>
                </form>
            </div>
        );
    } else if (isDetailView) {
        // ... (기존 상세 페이지 렌더링 + 상담 버튼 추가) ...
        if (!animalDetail) return <div>{message || "로딩 중..."}</div>;
        return (
            <div className="animal-detail-page">
                <h2>동물 상세 정보</h2>
                <div><strong>이름:</strong> {animalDetail.animalName}</div>
                <div><strong>견종:</strong> {animalDetail.animalBreed}</div>
                <div><strong>성별:</strong> {animalDetail.animalSex}</div>
                <div><strong>입소일:</strong> {animalDetail.animalDate}</div>
                <div><strong>특이사항:</strong> {animalDetail.animalContent}</div>
                <div><strong>상태:</strong> {animalDetail.animalState}</div>
                <div className="button-group">
                    {/* 일반 사용자에게만 보임 */}
                    {!isAdmin && (
                        <button onClick={handleStartChat} className="btn-chat-consult">동물 입양 상담</button>
                    )}
                </div>
                {isAdmin && (
                    <div className="button-group">
                        <button onClick={() => navigate(`/admin/animal/update/${animalDetail.animalId}`)}>수정</button>
                        <button onClick={handleDelete}>삭제</button>
                    </div>
                )}
                <button onClick={() => navigate(-1)}>이전</button>
            </div>
        );
    }

    return null; // 경로에 해당하지 않을 경우
};

export default AnimalForm;