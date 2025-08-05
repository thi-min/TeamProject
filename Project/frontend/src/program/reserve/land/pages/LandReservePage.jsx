import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

const LandReservePage = () => {
  const [formData, setFormData] = useState({
    name: "",
    phone1: "010",
    phone2: "",
    phone3: "",
    landType: "",
    animalNumber: "",
    guardianNumber: "",
    timeSlot: "",
    note: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleTimeSelect = (time) => {
    setFormData((prev) => ({ ...prev, timeSlot: time }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("예약 정보:", formData);
    // TODO: API 호출 등 예약 처리
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>놀이터 예약하기</h2>

      <form onSubmit={handleSubmit}>
        {/* 신청자명 */}
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

        <br />

        {/* 연락처 */}
        <label>
          연락처 <span style={{ color: "red" }}>*</span>
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
        </label>

        <br />

        {/* 놀이터 유형 */}
        <label>
          놀이터 유형 <span style={{ color: "red" }}>*</span>
          <select name="landType" value={formData.landType} onChange={handleChange} required>
            <option value="">선택</option>
            <option value="SMALL">소형견</option>
            <option value="LARGE">대형견</option>
          </select>
        </label>

        <br />

        {/* 반려견 수 */}
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

        <br />

        {/* 보호자 수 */}
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

        <br />

        {/* 시간대 선택 */}
        <div style={{ marginTop: "20px" }}>
          <strong>놀이터 시간 안내</strong>
          <div style={{ display: "flex", gap: "10px", marginTop: "10px" }}>
            {[
              { label: "09:00 ~ 11:00", info: "정원: 15/30" },
              { label: "11:00 ~ 13:00", info: "정원: 30/30" },
              { label: "13:00 ~ 15:00", info: "정원: 25/30" },
              { label: "15:00 ~ 18:00", info: "정원: 10/30" },
            ].map((slot) => (
              <button
                key={slot.label}
                type="button"
                onClick={() => handleTimeSelect(slot.label)}
                style={{
                  padding: "10px",
                  backgroundColor: formData.timeSlot === slot.label ? "#ccc" : "#eee",
                  border: "1px solid #aaa",
                  cursor: "pointer",
                }}
              >
                {slot.label}
                <br />
                {slot.info}
              </button>
            ))}
          </div>
        </div>

        <br />

        {/* 비고 */}
        <label>
          비고
          <textarea
            name="note"
            value={formData.note}
            onChange={handleChange}
            rows={3}
          />
        </label>

        <br />

        <div style={{ marginTop: "20px" }}>
          <button type="button" onClick={() => window.history.back()}>
            이전
          </button>
          <button type="submit" style={{ marginLeft: "10px" }}>
            다음
          </button>
        </div>
      </form>
    </div>
  );
};

export default LandReservePage;