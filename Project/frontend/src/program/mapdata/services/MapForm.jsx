import axios from 'axios';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../style/Map.css';

const MapForm = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        placeName: '',
        address: '',
        latitude: '',
        longitude: '',
        explanation: '',
    });

    const [message, setMessage] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!formData.placeName || !formData.address || !formData.latitude || !formData.longitude) {
            setMessage('필수 입력 항목을 모두 작성해주세요.');
            setTimeout(() => setMessage(null), 3000);
            return;
        }

        try {
            await axios.post('http://localhost:8080/api/mapdata', formData);
            alert('장소 정보가 성공적으로 등록되었습니다.');
            navigate('/land'); // 성공 후 맵 페이지로 이동
        } catch (error) {
            console.error('장소 정보 등록 실패:', error);
            setMessage('장소 정보 등록에 실패했습니다.');
        }
    };

    return (
        <div className="map-form-page">
            <div className="map-form-container">
                <h2 className="map-form-title">새 장소 등록</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-input-group">
                        <div className="form-input-item">
                            <label htmlFor="placeName" className="form-label required">장소 이름</label>
                            <input type="text" id="placeName" name="placeName" value={formData.placeName} onChange={handleChange} className="form-input" />
                        </div>
                        <div className="form-input-item">
                            <label htmlFor="address" className="form-label required">주소</label>
                            <input type="text" id="address" name="address" value={formData.address} onChange={handleChange} className="form-input" />
                        </div>
                        <div className="form-input-item">
                            <label htmlFor="latitude" className="form-label required">위도 (Latitude)</label>
                            <input type="number" step="any" id="latitude" name="latitude" value={formData.latitude} onChange={handleChange} className="form-input" />
                        </div>
                        <div className="form-input-item">
                            <label htmlFor="longitude" className="form-label required">경도 (Longitude)</label>
                            <input type="number" step="any" id="longitude" name="longitude" value={formData.longitude} onChange={handleChange} className="form-input" />
                        </div>
                        <div className="form-input-item-textarea">
                            <label htmlFor="explanation" className="form-label">설명</label>
                            <textarea id="explanation" name="explanation" value={formData.explanation} onChange={handleChange} className="form-textarea" rows="5" />
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

export default MapForm;