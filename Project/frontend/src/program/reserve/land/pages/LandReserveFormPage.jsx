import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import LandReserveService from "../services/LandReserveService";
import "./../style/LandReserveStyle.css";

const LandReserveFormPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const selectedDate = location.state?.selectedDate;

  // const [timeSlots, setTimeSlots] = useState([]);
  const [selectedSlotId, setSelectedSlotId] = useState(null);

  const timeSlots = [
    {
      timeSlotId: 1,
      label: "09:00 ~ 12:00",
      reservedCount: 5,
      capacity: 10,
    },
    {
      timeSlotId: 2,
      label: "13:00 ~ 16:00",
      reservedCount: 10,
      capacity: 10, // 정원 다 참
    },
    {
      timeSlotId: 3,
      label: "9:00 ~ 16:00 (점시시간포함)",
      reservedCount: 10,
      capacity: 10, 
    },
    {
      timeSlotId: 4,
      label: "15:00 ~ 18:00",
      reservedCount: 0,
      capacity: 10,
    },
  ];
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
    // LandReserveService.fetchReservationStatus(selectedDate)
    //   .then((res) => {
    //      setTimeSlots(res.data); // LandCountDto 리스트
    //   })
    //   .catch((err) => console.error(err));
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

      // ✅ 유효성 검사
      if (!formData.name.trim()) {
        alert("신청자명을 입력해주세요.");
        return;
      }

      if (!formData.phone2.trim() || !formData.phone3.trim()) {
        alert("연락처를 모두 입력해주세요.");
        return;
      }

      if (!formData.landType) {
        alert("놀이터 유형을 선택해주세요.");
        return;
      }

      if (!formData.animalNumber) {
        alert("반려견 수를 입력해주세요.");
        return;
      }

      if (!selectedDate) {
        alert("예약 날짜를 선택해주세요.");
        return;
      }

      if (!selectedSlotId) {
        alert("시간대를 선택해주세요.");
        return;
      }
    navigate("/reserve/land/confirm", {
      state: {
        name: formData.name,
        phone: `010-${formData.phone2}-${formData.phone3}`,
        landType: formData.landType,
        animalNumber: formData.animalNumber,
        guardianNumber: formData.guardianNumber,
        note: formData.note,
        selectedDate,
        selectedSlotId,
        timeSlots, 
      },
    });
      // ✅ 유효성 통과 후 예약 요청
      // const fullReserveRequestDto = {
      //   reserveDto: {
      //     memberNum: 1, // TODO: 실제 로그인 사용자 ID 사용
      //     reserveType: 1,
      //     reserveNumber: parseInt(formData.guardianNumber || 1),
      //     note: formData.note,
      //   },
      //   landDto: {
      //     landDate: selectedDate,
      //     timeSlotId: selectedSlotId,
      //     landType: formData.landType,
      //     animalNumber: parseInt(formData.animalNumber),
      //   },
      //   volunteerDto: null,
      // };

      // try {
      //   await LandReserveService.createReserve(fullReserveRequestDto);
      //   alert("예약이 완료되었습니다.");
      //   navigate("/mypage");
      // } catch (error) {
      //   console.error(error);
      //   alert("예약 중 오류가 발생했습니다.");
      // }
    };

  return (
    <div className="land-form-page">
      <h2 className="form-title">놀이터 예약신청</h2>
      <div className="required-info"><span className="required">*</span>표시된 부분은 필수 입력항목입니다.</div>

      <form className="form-container" onSubmit={handleSubmit}>
    <div className="form-wrapper">
      <p className="selected-date">
        선택한 날짜: <strong>{selectedDate}</strong>
      </p>

      {/* ▶️ 입력 정보 영역 */}
      <div className="form-section">
          <form className="form-container" onSubmit={handleSubmit}>
            {/* 신청자명 */}
            <div className="form-row">
              <label htmlFor="name">신청자명 <span className="required">*</span></label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>

            {/* 연락처 */}
            <div className="form-row">
              <label htmlFor="phone1">연락처 <span className="required">*</span></label>
              <div className="phone-input-wrapper">
                <select id="phone1" name="phone1" value={formData.phone1} onChange={handleChange}>
                  <option value="010">010</option>
                  <option value="011">011</option>
                </select>
                <span>-</span>
                <input
                  type="text"
                  name="phone2"
                  value={formData.phone2}
                  onChange={handleChange}
                  maxLength={4}
                  required
                />
                <span>-</span>
                <input
                  type="text"
                  name="phone3"
                  value={formData.phone3}
                  onChange={handleChange}
                  maxLength={4}
                  required
                />
              </div>
            </div>

            {/* 놀이터 유형 */}
            <div className="form-row">
              <label htmlFor="landType">놀이터 유형 <span className="required">*</span></label>
                <div className="radio-group">
                  <label>
                    <input
                      type="radio"
                      name="landType"
                      value="SMALL"
                      checked={formData.landType === "SMALL"}
                      onChange={handleChange}
                      required
                    />
                    소형견
                  </label>
                  <label>
                    <input
                      type="radio"
                      name="landType"
                      value="LARGE"
                      checked={formData.landType === "LARGE"}
                      onChange={handleChange}
                      required
                    />
                    대형견
                  </label>
                </div>
            </div>

            {/* 반려견 수 */}
            <div className="form-row">
              <label htmlFor="animalNumber">반려견 수 <span className="required">*</span></label>
              <input
                type="number"
                id="animalNumber"
                name="animalNumber"
                value={formData.animalNumber}
                onChange={handleChange}
                min={1}
                required
              />
            </div>

            {/* 보호자 수 */}
            <div className="form-row">
              <label htmlFor="guardianNumber">보호자 수 <span className="required">*</span></label>
              <input
                type="number"
                id="guardianNumber"
                name="guardianNumber"
                value={formData.guardianNumber}
                onChange={handleChange}
                min={1}
              />
            </div>
          </form>
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
          <textarea
            id="note"
            name="note"
            value={formData.note}
            onChange={handleChange}
            rows={3}
          />
        </div>
      </div>

      {/* ✅ 버튼 영역 (form 내부) */}
      <div className="form-action-buttons">
        <button className="prev-button" type="button" onClick={() => window.history.back()}>
          이전
        </button>
        <button className="next-button" type="submit">
          다음
        </button>
      </div>
    </div>
  </form>
    </div>
  );
};

export default LandReserveFormPage;
