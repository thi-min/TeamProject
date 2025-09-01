import { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { api } from "../../../common/api/axios.js";
import '../style/Adopt.css';
import Select from 'react-select';

const AdoptApplicationForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { id } = useParams();

    const [adopts, setAdopts] = useState([]);
    const [adoptDetail, setAdoptDetail] = useState(null);
    const [message, setMessage] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10;

    const [selectedMemberNum, setSelectedMemberNum] = useState('');
    const [members, setMembers] = useState([]);
    const [animals, setAnimals] = useState([]);

    const getRoleFromToken = () => {
        const token = localStorage.getItem('accessToken');
        if (!token) return null;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.role;
        } catch (e) {
            console.error("JWT 토큰 파싱 실패:", e);
            return null;
        }
    };
    const userRole = getRoleFromToken();
    const isAdmin = userRole === 'ADMIN';

    const isListView = location.pathname.includes('/adopt/list');
    const isDetailView = location.pathname.includes('/adopt/detail/');
    const isCreateView = location.pathname.includes('/adopt/regist') || location.pathname.includes('/adopt/request');
    const isUpdateView = location.pathname.includes('/adopt/update/');

    const authAxios = api.create({
        baseURL: 'http://localhost:8090/',
        headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    });

    const [userInput, setUserInput] = useState({
        adoptTitle: '',
        adoptContent: '',
        adoptState: 'ING',
        vistDt: '',
        consultDt: '',
        animalId: '',
    });

    const fetchAnimals = async () => {
        try {
            const response = await authAxios.get('/animals/list');
            setAnimals(response.data.content || []);
        } catch (error) {
            console.error("동물 목록 불러오기 실패:", error);
        }
    };

    const fetchMembers = async () => {
        if (!isAdmin) return;
        try {
            const response = await authAxios.get('/admin/membersList?page=0&size=100'); 
            setMembers(response.data.content || []);
        } catch (error) {
            console.error("회원 목록 불러오기 실패:", error);
            setMessage("회원 목록을 불러오는 중 오류가 발생했습니다.");
        }
    };

    const fetchAdopts = async (page) => {
        try {
            const response = await authAxios.get(`/adopts/list?page=${page}&size=${pageSize}`);
            setAdopts(response.data.content);
            setTotalPages(response.data.totalPages);
        } catch (error) {
            console.error("목록 조회 실패:", error);
            setMessage("목록을 불러올 수 없습니다. 권한을 확인해주세요.");
        }
    };

    const fetchAdoptDetail = async () => {
        if (!id) return;
        try {
            const response = await authAxios.get(`/adopts/detail/${id}`);
            const data = response.data;
            setAdoptDetail(data);
            if (isUpdateView) {
                setUserInput({
                    adoptTitle: data.adoptTitle,
                    adoptContent: data.adoptContent,
                    adoptState: data.adoptState,
                    vistDt: data.vistDt ? data.vistDt.substring(0, 10) : '',
                    consultDt: data.consultDt ? data.consultDt.substring(0, 16) : '',
                    animalId: data.animalId || '',
                });
                if (data.memberNum) setSelectedMemberNum(data.memberNum.toString());
            }
        } catch (error) {
            console.error("상세 정보 조회 실패:", error);
            setMessage("상세 정보를 불러올 수 없습니다.");
        }
    };

    useEffect(() => {
        if (isCreateView) {
            fetchAnimals();
            fetchMembers();
        }
    }, [isCreateView]);

    useEffect(() => {
        if (isListView) fetchAdopts(currentPage);
        else if (isDetailView || isUpdateView) fetchAdoptDetail();
    }, [location.pathname, currentPage, id]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUserInput(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (isCreateView && !userInput.animalId) {
            alert("입양 신청할 동물을 선택해주세요.");
            return;
        }
        if (isAdmin && !selectedMemberNum) {
            alert("입양자를 선택해주세요.");
            return;
        }

        try {
            const requestData = {
                ...userInput,
                animalId: parseInt(userInput.animalId, 10),
                memberNum: isAdmin ? parseInt(selectedMemberNum, 10) : undefined,
            };
            if (requestData.vistDt === "") requestData.vistDt = null;
            if (requestData.consultDt === "") requestData.consultDt = null;

            if (isCreateView) {
                await authAxios.post('/adopts/regist', requestData);
                alert("신청서가 성공적으로 제출되었습니다.");
            } else if (isUpdateView) {
                await authAxios.put(`/adopts/detail/${id}`, requestData);
                alert("신청서가 성공적으로 수정되었습니다.");
            }

            navigate(isAdmin ? '/admin/adopt/list' : '/member/adopt/list');
        } catch (error) {
            console.error('제출/수정 실패:', error);
            setMessage('제출/수정에 실패했습니다. 입력 정보를 확인해주세요.');
        }
    };

    const handleDelete = async () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await authAxios.delete(`/adopts/detail/${id}`);
                alert("삭제가 완료되었습니다.");
                navigate('/admin/adopt/list');
            } catch (error) {
                console.error("삭제 실패:", error);
                setMessage("삭제에 실패했습니다.");
            }
        }
    };

    const handlePageChange = (page) => setCurrentPage(page);

    // ------------------ JSX ------------------
    if (isListView) {
        return (
            <div className="adopt-list-page">
                 <div className="adopt-list-container">
                    <h2 className="adopt-list-title">{isAdmin ? "입양 신청서 관리" : "나의 입양 신청서"}</h2>
                    {isAdmin && (
                        <div className="button-container">
                            <button onClick={() => navigate('/admin/adopt/regist')} className="btn-create-adopt">
                                입양 신청서 작성
                            </button>
                        </div>
                    )}
                    
                    <table className="table type2 responsive border">
                        <thead>
                            <tr>
                                <th>제목</th>
                                <th>입양자명</th>
                                <th>입양 동물명</th>
                                <th>상담 날짜</th>
                                <th>상태</th>
                            </tr>
                        </thead>
                        <tbody>
                            {adopts.map((adopt) => (
                                <tr key={adopt.adoptNum} 
                                    onClick={() => navigate(isAdmin ? `/admin/adopt/detail/${adopt.adoptNum}` : `/member/adopt/detail/${adopt.adoptNum}`)}
                                    style={{ cursor: 'pointer' }}
                                >
                                    <td>{adopt.adoptTitle}</td>
                                    <td>{adopt.memberName}</td>
                                    <td>{adopt.animalName}</td>
                                    <td>{adopt.consultDt ? new Date(adopt.consultDt).toLocaleDateString() : 'N/A'}</td>
                                    <td>{adopt.adoptState}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    
                    {isAdmin && (
                        <div className="pagination">
                            {[...Array(totalPages).keys()].map(page => (
                                <button
                                    key={page}
                                    onClick={() => handlePageChange(page)}
                                    className={currentPage === page ? 'active' : ''}
                                >
                                    {page + 1}
                                </button>
                            ))}
                        </div>
                    )}
                </div>
            </div>
        );
    } else if (isCreateView || isUpdateView) {
        return (
            <div className="adopt-form-page">
                <h2>{isCreateView ? "입양 신청서 작성" : "입양 신청서 수정"}</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        name="adoptTitle"
                        value={userInput.adoptTitle}
                        onChange={handleChange}
                        placeholder="제목"
                        required
                    />
                    <textarea
                        name="adoptContent"
                        value={userInput.adoptContent}
                        onChange={handleChange}
                        placeholder="상담 내용"
                        required
                    />

                    <label>방문 예정일:</label>
                    <input
                        type="date"
                        name="vistDt"
                        value={userInput.vistDt}
                        onChange={handleChange}
                    />

                    {/* 동물 선택 */}
                    <label>입양할 동물 선택:</label>
                    <Select
                        options={animals.map(a => ({ value: a.animalId, label: a.animalName }))}
                        value={userInput.animalId ? {
                            value: parseInt(userInput.animalId),
                            label: animals.find(a => a.animalId === parseInt(userInput.animalId))?.animalName
                        } : null}
                        onChange={option => setUserInput(prev => ({ ...prev, animalId: option.value }))}
                        placeholder="입양할 동물을 선택하세요"
                        isSearchable={true}
                    />

                    {/* 관리자만 입양자 선택 및 상담/상태 */}
                    {isAdmin && (
                        <>
                            <label>입양자 선택:</label>
                            <Select
                                options={members.map(m => ({
                                    value: m.memberNum,
                                    label: `${m.memberName} (${m.memberId})`
                                }))}
                                value={selectedMemberNum ? {
                                    value: parseInt(selectedMemberNum),
                                    label: `${members.find(m => m.memberNum === parseInt(selectedMemberNum))?.memberName} (${members.find(m => m.memberNum === parseInt(selectedMemberNum))?.memberId})`
                                } : null}
                                onChange={option => setSelectedMemberNum(option.value)}
                                placeholder="회원 검색 및 선택"
                                isSearchable={true}
                                filterOption={(option, input) => 
                                    option.label.toLowerCase().includes(input.toLowerCase())
                                }
                            />

                            <label>상담 날짜/시간:</label>
                            <input
                                type="datetime-local"
                                name="consultDt"
                                value={userInput.consultDt}
                                onChange={handleChange}
                            />

                            <label>입양 상태:</label>
                            <select
                                name="adoptState"
                                value={userInput.adoptState}
                                onChange={handleChange}
                            >
                                <option value="ING">진행 중</option>
                                <option value="DONE">완료</option>
                                <option value="REJ">거절</option>
                            </select>
                        </>
                    )}

                    <button type="submit">{isCreateView ? "제출" : "수정"}</button>
                    <button type="button" onClick={() => navigate(-1)}>이전</button>
                </form>
            </div>
        );
    } else if (isDetailView) {
        if (!adoptDetail) return <div>{message || "로딩 중..."}</div>;
        return (
            <div className="adopt-detail-page">
                <h2>입양 신청서 상세 조회</h2>
                <div><strong>입양자명:</strong> {adoptDetail.memberName}</div>
                <div><strong>입양 동물명:</strong> {adoptDetail.animalName}</div>
                <div><strong>상담 날짜:</strong> {adoptDetail.consultDt ? new Date(adoptDetail.consultDt).toLocaleDateString() : 'N/A'}</div>
                <div><strong>내용:</strong> {adoptDetail.adoptContent}</div>
                <div><strong>상태:</strong> {adoptDetail.adoptState}</div>
                {isAdmin && (
                    <div className="button-group">
                        <button onClick={() => navigate(`/admin/adopt/update/${adoptDetail.adoptNum}`)}>수정</button>
                        <button onClick={handleDelete}>삭제</button>
                    </div>
                )}
                <button onClick={() => navigate(-1)}>이전</button>
            </div>
        );
    }

    return null;

};

export default AdoptApplicationForm;