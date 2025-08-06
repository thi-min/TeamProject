import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import LandReserveService from "../services/LandReserveService";
import "./../style/LandReserveStyle.css";

const LandReserveFormPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const selectedDate = location.state?.selectedDate;

  const [timeSlots, setTimeSlots] = useState([]);
  const [selectedSlotId, setSelectedSlotId] = useState(null);
  const [formData, setFormData] = useState({
    name: "",
    phone1: "010",
    phone2: "",
    phone3: "",
    landType: "",
    animalNumber: "",
    guardianNumber: "",
    note: "",
  });

  useEffect(() => {
    if (!selectedDate) return;
    // 날짜에 해당하는 시간대 조회
    LandReserveService.fetchReservationStatus(selectedDate)
      .then((res) => {
        setTimeSlots(res.data); // LandCountDto 리스트
      })
      .catch((err) => console.error(err));
  }, [selectedDate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleTimeSelect = (slotId) => {
    setSelectedSlotId(slotId);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!selectedDate || !selectedSlotId) {
      alert("날짜와 시간대를 선택해주세요.");
      return;
    }

    const fullReserveRequestDto = {
      reserveDto: {
        memberNum: 1, // TODO: 실제 로그인 사용자 ID 사용
        reserveType: 1,
        reserveNumber: parseInt(formData.guardianNumber || 1),
        note: formData.note,
      },
      landDto: {
        landDate: selectedDate,
        timeSlotId: selectedSlotId,
        landType: formData.landType,
        animalNumber: parseInt(formData.animalNumber),
      },
      volunteerDto: null,
    };

    try {
      await LandReserveService.createReserve(fullReserveRequestDto);
      alert("예약이 완료되었습니다.");
      navigate("/mypage"); // 예: 마이페이지로 이동
    } catch (error) {
      console.error(error);
      alert("예약 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="land-form-page">
      <h2>놀이터 예약하기</h2>
      <p>선택한 날짜: <strong>{selectedDate}</strong></p>

      <form className="land-form" onSubmit={handleSubmit}>
        <label>
          신청자명 <span style={{ color: "red" }}>*</span>
          <input
            type="text"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
          />
        </label>

        <label>
          연락처 <span style={{ color: "red" }}>*</span>
          <div style={{ display: "flex", gap: "4px" }}>
            <select name="phone1" value={formData.phone1} onChange={handleChange}>
              <option value="010">010</option>
              <option value="011">011</option>
            </select>
            -
            <input
              type="text"
              name="phone2"
              value={formData.phone2}
              onChange={handleChange}
              maxLength={4}
              required
            />
            -
            <input
              type="text"
              name="phone3"
              value={formData.phone3}
              onChange={handleChange}
              maxLength={4}
              required
            />
          </div>
        </label>

        <label>
          놀이터 유형 <span style={{ color: "red" }}>*</span>
          <select name="landType" value={formData.landType} onChange={handleChange} required>
            <option value="">선택</option>
            <option value="SMALL">소형견</option>
            <option value="LARGE">대형견</option>
          </select>
        </label>

        <label>
          반려견 수 <span style={{ color: "red" }}>*</span>
          <input
            type="number"
            name="animalNumber"
            value={formData.animalNumber}
            onChange={handleChange}
            min={1}
            required
          />
        </label>

        <label>
          보호자 수
          <input
            type="number"
            name="guardianNumber"
            value={formData.guardianNumber}
            onChange={handleChange}
            min={1}
          />
        </label>

        <div>
          <strong>시간대 선택</strong>
          <div className="time-slot-group">
            {timeSlots.map((slot) => (
              <button
                key={slot.timeSlotId}
                type="button"
                onClick={() => handleTimeSelect(slot.timeSlotId)}
                disabled={slot.reservedCount >= slot.capacity}
                className={`time-slot-button ${selectedSlotId === slot.timeSlotId ? "selected" : ""}`}
              >
                {slot.label}<br />정원: {slot.reservedCount}/{slot.capacity}
              </button>
            ))}
          </div>
        </div>

        <label>
          비고
          <textarea
            name="note"
            value={formData.note}
            onChange={handleChange}
            rows={3}
          />
        </label>

        <div className="calendar-buttons">
          <button className="prev-button" type="button" onClick={() => window.history.back()}>
            이전
          </button>
          <button className="next-button" type="submit">
            예약하기
          </button>
      </div>
      </form>
    </div>
  );
};

export default LandReserveFormPage;
