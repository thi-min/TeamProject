import axios from 'axios';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../style/Animal.css';

const AnimalForm = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        animalName: '',
        animalBreed: '',
        animalSex: 'MALE', // Enum 기본값 설정
        animalDate: '', // 입소일 (LocalDate)
        animalContent: '', // 상세 내용
        animalState: 'WAIT', // Enum 기본값 설정
        adoptDate: '', // 입양일 (LocalDate, 선택 사항)
    });

    const [message, setMessage] = useState(null);
    const [file, setFile] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleFileChange = (e) => {
        setFile(e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // 필수 입력 항목 유효성 검사
        if (!formData.animalName || !formData.animalBreed || !formData.animalDate) {
            setMessage('필수 입력 항목(이름, 품종, 입소일)을 모두 작성해주세요.');
            setTimeout(() => setMessage(null), 3000);
            return;
        }

        try {
            // 1. 동물 정보 등록
            const animalResponse = await axios.post('http://localhost:8080/api/animals', formData);
            const animalId = animalResponse.data.animalId;

            // 2. 파일이 있다면 파일 업로드
            if (file) {
                const fileData = new FormData();
                fileData.append('file', file);
                
                await axios.post(`http://localhost:8080/api/animals/${animalId}/files`, fileData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
            }

            alert('동물 정보가 성공적으로 등록되었습니다.');
            navigate('/admin/dashboard'); // 성공 후 관리자 대시보드로 이동
        } catch (error) {
            console.error('동물 정보 등록 실패:', error);
            setMessage('동물 정보 등록에 실패했습니다.');
        }
    };

    return (
        <div className="application-form-page">
            <div className="application-form-container">
                <h2 className="application-form-title">동물 정보 기입</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-input-group">
                        <div className="form-input-item">
                            <label htmlFor="animalName" className="form-label required">동물 이름</label>
                            <input type="text" id="animalName" name="animalName" value={formData.animalName} onChange={handleChange} className="form-input" />
                        </div>
                        
                        <div className="form-input-item">
                            <label htmlFor="animalBreed" className="form-label required">품종</label>
                            <input type="text" id="animalBreed" name="animalBreed" value={formData.animalBreed} onChange={handleChange} className="form-input" />
                        </div>

                        <div className="form-input-item">
                            <label htmlFor="animalSex" className="form-label required">성별</label>
                            <select id="animalSex" name="animalSex" value={formData.animalSex} onChange={handleChange} className="form-select">
                                <option value="MALE">수컷</option>
                                <option value="FEMALE">암컷</option>
                            </select>
                        </div>
                        
                        <div className="form-input-item">
                            <label htmlFor="animalDate" className="form-label required">입소일</label>
                            <input type="date" id="animalDate" name="animalDate" value={formData.animalDate} onChange={handleChange} className="form-input" />
                        </div>

                        <div className="form-input-item-textarea">
                            <label htmlFor="animalContent" className="form-label">특징 및 상세 내용</label>
                            <textarea id="animalContent" name="animalContent" value={formData.animalContent} onChange={handleChange} className="form-textarea" rows="5" />
                        </div>

                        <div className="form-input-item">
                            <label htmlFor="file" className="form-label">사진 등록</label>
                            <input type="file" id="file" name="file" onChange={handleFileChange} className="form-input" />
                        </div>
                    </div>

                    <div className="form-buttons">
                        <button
                            type="button"
                            onClick={() => navigate(-1)}
                            className="form-button-secondary"
                        >
                            취소
                        </button>
                        <button
                            type="submit"
                            className="form-button-primary"
                        >
                            등록
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

export default AnimalForm;