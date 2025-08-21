import axios from 'axios';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../style/Adopt.css'; // 관리자 페이지용 스타일 파일 (가정)

const AdoptApplicationForm = () => {
    const navigate = useNavigate();
    // URL에서 회원 번호와 동물 번호를 가져옵니다.
    // URL 경로는 '/adopt/form/:memberNum/:animalId'와 같이 구성되어야 합니다.
    const { memberNum, animalId } = useParams(); 

    // 사용자가 입력할 필드들을 위한 상태
    const [userInput, setUserInput] = useState({
        adoptName: '',
        adoptPhone: '',
        adoptAddr: '',
        adoptedDogName: '',
        adoptDetail: '',
    });

    const [message, setMessage] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUserInput(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 필수 입력 항목 유효성 검사
        if (!userInput.adoptName || !userInput.adoptPhone || !userInput.adoptedDogName) {
            setMessage('필수 입력 항목을 모두 작성해주세요.');
            setTimeout(() => setMessage(null), 3000); 
            return;
        }

        // 백엔드 AdoptRequestDto 형식에 맞게 데이터 조합
        const requestData = {
            adoptTitle: `입양 신청 - ${userInput.adoptedDogName} (신청자: ${userInput.adoptName})`,
            adoptContent: `
                입양 희망자 이름: ${userInput.adoptName}
                연락처: ${userInput.adoptPhone}
                주소: ${userInput.adoptAddr}
                
                상세 내용:
                ${userInput.adoptDetail}
            `.trim(), // 불필요한 공백 제거
            
            // DTO에 정의된 필드에 맞게 값을 할당
            memberNum: memberNum ? parseInt(memberNum, 10) : null,
            animalId: animalId ? parseInt(animalId, 10) : null,
            adoptState: 'REQUEST' 
        };

        try {
            // 정확한 백엔드 API 엔드포인트(/api/adopts)로 POST 요청 전송
            await axios.post('http://localhost:8080/api/adopts', requestData); 
            
            alert('입양 신청서가 성공적으로 제출되었습니다.');
            // 성공 후 리디렉션할 페이지 설정
            navigate('/adoption-success'); 
        } catch (error) {
            console.error('입양 신청서 제출 실패:', error);
            setMessage('입양 신청서 제출에 실패했습니다.');
        }
    };

    return (
        <div className="application-form-page">
            <div className="application-form-container">
                <h2 className="application-form-title">입양 신청서 작성</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-input-group">
                        <div className="temp_form md">
                            <label htmlFor="adoptName" className="form-label required">입양 희망자 이름</label>  
                            <input type="text" id="adoptName" name="adoptName" value={userInput.adoptName} onChange={handleChange} className="temp_input" placeholder="이름"/>
                        </div>
                        
                        <div className="temp_form md">
                            <label htmlFor="adoptPhone" className="form-label required">연락처</label>
                            <input type="text" id="adoptPhone" name="adoptPhone" value={userInput.adoptPhone} onChange={handleChange} className="temp_input" placeholder="연락처"/>
                        </div>

                        <div className="temp_form md">
                            <label htmlFor="adoptAddr" className="form-label">주소</label>
                            <input type="text" id="adoptAddr" name="adoptAddr" value={userInput.adoptAddr} onChange={handleChange} className="temp_input" placeholder="주소"/>
                        </div>

                        <div className="temp_form md">
                            <label htmlFor="adoptedDogName" className="form-label required">입양하는 동물 이름</label>
                            <input type="text" id="adoptedDogName" name="adoptedDogName" value={userInput.adoptedDogName} onChange={handleChange} className="temp_input" placeholder="동물 이름"/>
                        </div>
                        
                        <div className="form-input-item-textarea">
                            <label htmlFor="adoptDetail" className="form-label">신청 상세 내용</label>
                            <textarea id="adoptDetail" name="adoptDetail" value={userInput.adoptDetail} onChange={handleChange} className="form-textarea" rows="5" />
                        </div>
                    </div>

                    <div className="form-buttons">
                        <button type="button" onClick={() => navigate(-1)} className="form-button-secondary">
                            이전
                        </button>
                        
                        <button type="submit" className="form-button-primary">
                            신청서 제출
                        </button>
                    </div>
                </form>
            </div>
            {message && (
                <div className="form-message">
                    {message}
                </div>
            )}
        </div>
    );
};

export default AdoptApplicationForm;