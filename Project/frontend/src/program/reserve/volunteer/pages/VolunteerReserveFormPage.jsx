import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./../style/VolunteerReserveStyle.css";

const VolunteerReserveFormPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const selectedDate = location.state?.selectedDate;

  const [selectedSlotId, setSelectedSlotId] = useState(null);

  const timeSlots = [
    { timeSlotId: 1, label: "09:00 ~ 12:00 (오전)", reservedCount: 5, capacity: 10 },
    { timeSlotId: 2, label: "13:00 ~ 16:00 (오후)", reservedCount: 10, capacity: 10 },
    { timeSlotId: 3, label: "09:00 ~ 16:00 (점심시간 12:00 ~13:00 제외)", reservedCount: 2, capacity: 10 },
  ];

  const [formData, setFormData] = useState({
    name: "",
    phone1: "010",
    phone2: "",
    phone3: "",
    birth: "",
    reserveNumber: "",
    note: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleTimeSelect = (slotId) => {
    setSelectedSlotId(slotId);
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // ✅ 유효성 검사
    if (!formData.name.trim()) return alert("신청자명을 입력해주세요.");
    if (!formData.phone2.trim() || !formData.phone3.trim()) return alert("연락처를 모두 입력해주세요.");
    if (!formData.birth) return alert("생년월일을 선택해주세요.");
    if (!formData.reserveNumber) return alert("신청 인원 수를 선택해주세요.");
    if (!selectedSlotId) return alert("시간대를 선택해주세요.");

    navigate("/reserve/volunteer/confirm", {
      state: {
        name: formData.name,
        phone: `010-${formData.phone2}-${formData.phone3}`,
        birth: formData.birth,
        reserveNumber: formData.reserveNumber,
        note: formData.note,
        selectedDate,
        selectedSlotId,
        timeSlots,
      },
    });
  };

  return (
    <div className="volunteer-form-page">
      <h2 className="form-title">봉사활동 신청서</h2>
      <div className="required-info"><span className="required">*</span>표시된 부분은 필수 입력항목입니다.</div>

      <form className="form-container" onSubmit={handleSubmit}>
        <div className="form-wrapper">
          <p className="selected-date">선택한 날짜: <strong>{selectedDate}</strong></p>

          {/* ▶️ 신청 정보 입력 */}
          <div className="form-section">
            {/* 신청자명 */}
            <div className="form-row">
              <label htmlFor="name">신청자명 <span className="required">*</span></label>
              <input type="text" name="name" value={formData.name} onChange={handleChange} required />
            </div>

            {/* 연락처 */}
            <div className="form-row">
              <label htmlFor="phone1">연락처 <span className="required">*</span></label>
              <div className="phone-input-wrapper">
                <select name="phone1" value={formData.phone1} onChange={handleChange}>
                  <option value="010">010</option>
                  <option value="011">011</option>
                </select>
                <span>-</span>
                <input type="text" name="phone2" maxLength={4} value={formData.phone2} onChange={handleChange} required />
                <span>-</span>
                <input type="text" name="phone3" maxLength={4} value={formData.phone3} onChange={handleChange} required />
              </div>
            </div>

            {/* 생년월일 */}
            <div className="form-row">
              <label htmlFor="birth">생년월일 <span className="required">*</span></label>
              <input type="date" name="birth" value={formData.birth} onChange={handleChange} required />
            </div>

            {/* 신청 인원 수 */}
            <div className="form-row">
              <label htmlFor="reserveNumber">신청 인원 수 <span className="required">*</span></label>
              <select name="reserveNumber" value={formData.reserveNumber} onChange={handleChange} required>
                <option value="">선택</option>
                {[...Array(10)].map((_, i) => (
                  <option key={i + 1} value={i + 1}>{i + 1}명</option>
                ))}
              </select>
            </div>
          </div>

          {/* ▶️ 시간대 선택 */}
          <div className="form-section">
            <div className="form-row">
              <label>시간대 선택 <span className="required">*</span></label>
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
          </div>

          {/* ▶️ 비고 입력 */}
          <div className="form-section">
            <div className="form-row">
              <label htmlFor="note">비고</label>
              <textarea name="note" rows={3} value={formData.note} onChange={handleChange} />
            </div>
          </div>

          {/* ▶️ 버튼 */}
          <div className="form-action-buttons">
            <button className="prev-button" type="button" onClick={() => window.history.back()}>이전</button>
            <button className="next-button" type="submit">다음</button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default VolunteerReserveFormPage;