import axios from 'axios';
import { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import '../style/Adopt.css';

const AdoptApplicationForm = () => {
    const navigate = useNavigate();
    const location = useLocation();
    const { id, memberNum, animalId } = useParams();

    const [adopts, setAdopts] = useState([]);
    const [adoptDetail, setAdoptDetail] = useState(null);
    const [message, setMessage] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 10;
    
    // JWT 토큰에서 역할 정보 추출
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

    // 현재 URL 경로에 따른 모드 판단
    const isListView = location.pathname === '/admin/adopt/list' || location.pathname === '/member/adopt/list';
    const isDetailView = location.pathname.startsWith('/admin/adopt/detail/') || location.pathname.startsWith('/member/adopt/detail/');
    const isCreateView = location.pathname.startsWith('/adopt/request/') || location.pathname.startsWith('/admin/adopt/resist');
    const isUpdateView = location.pathname.startsWith('/admin/adopt/update/');
    
    // API 요청을 위한 기본 Axios 설정 (인터셉터 사용 시 이 부분은 불필요)
    const authAxios = axios.create({
        baseURL: 'http://localhost:8080/api',
        headers: {
            Authorization: `Bearer ${localStorage.getItem('accessToken')}`
        }
    });

    // 폼 입력 상태
    const [userInput, setUserInput] = useState({
        adoptTitle: '',
        adoptContent: '',
        adoptState: 'ING',
        vistDt: '',
        consultDt: '',
    });

    const fetchAdopts = async (page) => {
        try {
            // 백엔드에서 권한을 처리하므로, 요청 URL은 단순화
            const response = await authAxios.get(`/adopts?page=${page}&size=${pageSize}`);
            
            if (isAdmin) {
                setAdopts(response.data.content);
                setTotalPages(response.data.totalPages);
            } else {
                setAdopts(response.data);
            }
        } catch (error) {
            console.error("목록 조회 실패:", error);
            setMessage("목록을 불러올 수 없습니다. 권한을 확인해주세요.");
        }
    };

    const fetchAdoptDetail = async () => {
        if (!id) return;
        try {
            const response = await authAxios.get(`/adopts/${id}`);
            const data = response.data;
            setAdoptDetail(data);
            if (isUpdateView) {
                setUserInput({
                    adoptTitle: data.adoptTitle,
                    adoptContent: data.adoptContent,
                    adoptState: data.adoptState,
                });
            }
        } catch (error) {
            console.error("상세 정보 조회 실패:", error);
            setMessage("상세 정보를 불러올 수 없습니다.");
        }
    };
    
    // 폼 입력 변경 핸들러
    const handleChange = (e) => {
        const { name, value } = e.target;
        setUserInput(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const requestData = {
                ...userInput,
                adoptNum: isUpdateView ? id : undefined,
                memberNum: memberNum ? parseInt(memberNum, 10) : undefined,
                animalId: animalId ? parseInt(animalId, 10) : undefined,
            };
            
            if (isCreateView) {
                await authAxios.post('/adopts', requestData);
                alert("신청서가 성공적으로 제출되었습니다.");
            } else if (isUpdateView) {
                await authAxios.put(`/adopts/${id}`, requestData);
                alert("신청서가 성공적으로 수정되었습니다.");
            }
            navigate(isAdmin ? '/admin/adopt/list' : '/member/adopt/list');
        } catch (error) {
            console.error('제출/수정 실패:', error);
            setMessage('제출/수정에 실패했습니다.');
        }
    };

    const handleDelete = async () => {
        if (window.confirm("정말 삭제하시겠습니까?")) {
            try {
                await authAxios.delete(`/adopts/${id}`);
                alert("삭제가 완료되었습니다.");
                navigate('/admin/adopt/list');
            } catch (error) {
                console.error("삭제 실패:", error);
                setMessage("삭제에 실패했습니다.");
            }
        }
    };

    // 데이터 로딩 로직
    useEffect(() => {
        if (isListView) {
            fetchAdopts(currentPage);
        } else if (isDetailView || isUpdateView) {
            fetchAdoptDetail();
        }
    }, [location.pathname, currentPage, id]);

    // 페이지 변경 핸들러
    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    // ------------------ JSX 렌더링 부분 ------------------
    if (isListView) {
        return (
            <div className="adopt-list-page">
                <div className="adopt-list-container">
                    <h2 className="adopt-list-title">{isAdmin ? "입양 신청서 관리" : "나의 입양 신청서"}</h2>
                    {isAdmin && (
                        <div className="button-container">
                            <button onClick={() => navigate('/admin/adopt/resist')} className="btn-create-adopt">
                                입양 신청서 작성
                            </button>
                        </div>
                    )}
                    
                    <table className="adopt-table">
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
                    <input type="text" name="adoptTitle" value={userInput.adoptTitle} onChange={handleChange} placeholder="제목" required />
                    <textarea name="adoptContent" value={userInput.adoptContent} onChange={handleChange} placeholder="상담 내용" required />
                    <label>방문 예정일:</label>
                    <input type="date" name="vistDt" value={userInput.vistDt} onChange={handleChange} />
                    
                    {isAdmin && (
                        <>
                            <label>상담 날짜/시간:</label>
                            <input type="datetime-local" name="consultDt" value={userInput.consultDt} onChange={handleChange} />
                            
                            <label>입양 상태:</label>
                            <select name="adoptState" value={userInput.adoptState} onChange={handleChange}>
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
                {/* 관리자에게만 보이는 버튼 */}
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

    return null; // 경로에 해당하지 않을 경우
};

export default AdoptApplicationForm;