import axios from 'axios';
import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../style/Adopt.css'; // 관리자 페이지용 스타일 파일 (가정)


const AdoptApplicationForm = () => {
    const navigate = useNavigate();
    const { memberNum } = useParams(); // URL에서 회원 번호를 가져옵니다.

    const [formData, setFormData] = useState({
        adopt_name: '',
        adopt_phone: '',
        adopt_addr: '',
        adopt_detail: '',
        adopted_dog_name: '',
        adopted_dog_num: '',
        memberNum: memberNum,
        status: 'REQUEST' // 기본 상태는 'REQUEST'로 설정
    });

    const [message, setMessage] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.adopt_name || !formData.adopt_phone || !formData.adopted_dog_name) {
            setMessage('필수 입력 항목을 모두 작성해주세요.');
            setTimeout(() => setMessage(null), 3000); 
            return;
        }

        try {
            await axios.post('http://localhost:8080/api/adopt/request', formData); // 백엔드 API 주소로 변경 필요
            alert('입양 신청서가 성공적으로 제출되었습니다.');
            navigate('/admin/chat/list'); // 성공 후 채팅 목록 페이지로 이동
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
                            <label htmlFor="adopt_name" className="form-label required">입양 희망자 이름</label>   
                            <input type="text" id="adopt_name" name="adopt_name" value={formData.adopt_name} onChange={handleChange} class="temp_input" placeholder="이름"/>
                        </div>
                        
                        <div className="temp_form md">
                            <label htmlFor="adopt_phone" className="form-label required">연락처</label>
                            <input type="text" id="adopt_phone" name="adopt_phone" value={formData.adopt_phone} onChange={handleChange} class="temp_input" placeholder="연락처"/>
                        </div>

                        <div className="temp_form md">
                            <label htmlFor="adopt_addr" className="form-label">주소</label>
                            <input type="text" id="adopt_addr" name="adopt_addr" value={formData.adopt_addr} onChange={handleChange} class="temp_input" placeholder="주소"/>
                        </div>

                        <div className="temp_form md">
                            <label htmlFor="adopted_dog_name" className="form-label required">입양하는 동물 이름</label>
                            <input type="text" id="adopted_dog_name" name="adopted_dog_name" value={formData.adopted_dog_name} onChange={handleChange} class="temp_input" placeholder="동물 이름"/>
                        </div>

                        <div className="temp_form md">
                            <label htmlFor="adopted_dog_num" className="form-label">입양하는 동물 번호</label>
                            <input type="number" id="adopted_dog_num" name="adopted_dog_num" value={formData.adopted_dog_num} onChange={handleChange} class="temp_input" placeholder="동물 번호"/>
                        </div>
                        
                        <div className="form-input-item-textarea">
                            <label htmlFor="adopt_detail" className="form-label">신청 상세 내용</label>
                            <textarea id="adopt_detail" name="adopt_detail" value={formData.adopt_detail} onChange={handleChange} className="form-textarea" rows="5" />
                        </div>
                    </div>

                    <div className="form-buttons">
                        <button
                            type="button"
                            onClick={() => navigate(-1)} // 이전 페이지로 돌아가기
                            className="form-button-secondary"
                        >
                            이전
                        </button>
                        
                        <button
                            type="submit"
                            className="form-button-primary"
                        >
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