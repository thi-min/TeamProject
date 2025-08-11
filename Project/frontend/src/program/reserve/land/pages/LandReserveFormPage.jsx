import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import LandReserveService from "../services/LandReserveService";
import "./../style/LandReserveStyle.css";

const toDateStr = (d) => (typeof d === "string" ? d : new Date(d).toISOString().slice(0, 10));
const TYPE_LABELS = {
  SMALL: ["09:00 ~ 11:00", "14:00 ~ 16:00"],
  LARGE: ["12:00 ~ 14:00", "16:00 ~ 18:00"],
};

const LandReserveFormPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const selectedDate = location.state?.selectedDate
    ? toDateStr(location.state.selectedDate)
    : "";

  // TODO: 실제 로그인 사용자 memberNum로 교체
  const memberNum = 1;

  const [timeSlots, setTimeSlots] = useState([]); // 서버 응답 표준화 배열
  const [displaySlots, setDisplaySlots] = useState([]); // 화면 표시용 슬롯
  const [selectedSlotId, setSelectedSlotId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const [formData, setFormData] = useState({
    name: "",
    phone1: "010",
    phone2: "",
    phone3: "",
    landType: "", // "SMALL" | "LARGE"
    animalNumber: "",
    guardianNumber: "",
    note: "",
  });

  /** LandCountDto -> 표준형 변환 */
  const normalizeCountDto = (arr = []) =>
    arr.map((s) => ({
      timeSlotId: s.timeSlotId,
      label: s.label,
      capacity: s.capacity ?? 0,
      reservedCount: s.reservedCount ?? 0,
      enabled: true,
    }));

  /** TimeSlotDto -> 표준형 변환(폴백) */
  const normalizeSlotDto = (arr = []) =>
    arr.map((s) => ({
      timeSlotId: s.id,
      label: s.label,
      capacity: s.capacity ?? 0,
      reservedCount: 0,
      enabled: s.enabled ?? true,
    }));

  /** 시간대 데이터 로드 */
  useEffect(() => {
  let mounted = true;

  const loadSlots = async () => {
    if (!selectedDate) {
      setDisplaySlots([]);
      return;
    }

    try {
      setLoading(true);
      setErrorMsg("");

      // landType 선택된 경우에만 예약 현황 API 호출
      if (formData.landType) {
        try {
          const res = await LandReserveService.fetchReservationStatus(
            selectedDate,
            memberNum,
            formData.landType
          );
          if (mounted) {
            const data = normalizeCountDto(res.data);
            setTimeSlots(data);
            setDisplaySlots(data);
            setLoading(false);
            return;
          }
        } catch (err) {
          console.error("예약 현황 API 실패:", err);
        }
      }

      // 폴백 - 전체 시간대 호출
      const res2 = await LandReserveService.fetchTimeSlots();
      if (mounted) {
        const data = normalizeSlotDto(res2.data);
        setTimeSlots(data);
        setDisplaySlots(data);
      }
    } catch (err2) {
      console.error("시간대 목록 API 실패:", err2);
      if (mounted) setErrorMsg("시간대 목록을 불러오지 못했습니다.");
    } finally {
      if (mounted) setLoading(false); // 로딩 해제
    }
  };

  loadSlots();
  return () => {
    mounted = false;
  };
}, [selectedDate, formData.landType, memberNum]);

  /** 입력 변경 핸들러 */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  /** 시간대 선택 */
  const handleTimeSelect = (slotId) => setSelectedSlotId(slotId);

  /** 제출 처리 */
  const handleSubmit = (e) => {
    e.preventDefault();

    // 유효성 검사
    if (!formData.name.trim()) return alert("신청자명을 입력해 주세요.");
    if (!formData.phone2.trim() || !formData.phone3.trim())
      return alert("연락처를 모두 입력해 주세요.");
    if (!formData.landType) return alert("놀이터 유형을 선택해 주세요.");
    if (!formData.animalNumber) return alert("반려견 수를 입력해 주세요.");
    if (!selectedDate) return alert("예약 날짜를 선택해 주세요.");
    if (!selectedSlotId) return alert("시간대를 선택해 주세요.");

    // 확인 페이지로 이동
    navigate("/reserve/land/confirm", {
      state: {
        ...formData,
        phone: `${formData.phone1}-${formData.phone2}-${formData.phone3}`,
        selectedDate,
        selectedSlotId,
        timeSlots: displaySlots,
      },
    });
  };

  const filteredSlots = React.useMemo(() => {
    if (!formData.landType) return [];      // 미선택 시 안 보이게
    const allow = TYPE_LABELS[formData.landType] ?? [];
    return displaySlots.filter(s => allow.includes((s.label || "").trim()));
  }, [displaySlots, formData.landType]);

  if (loading) return <div className="land-form-page">시간대를 불러오는 중입니다…</div>;
  if (errorMsg) return <div className="land-form-page">{errorMsg}</div>;
  return (
    <div className="land-form-page">
      <h2 className="form-title">놀이터 예약신청</h2>
      <div className="required-info">
        <span className="required">*</span>표시는 필수 입력항목입니다.
      </div>

      <form className="form-container" onSubmit={handleSubmit}>
        <div className="form-wrapper">
          <p className="selected-date">
            선택한 날짜: <strong>{selectedDate || "-"}</strong>
          </p>

          {/* 신청자명 */}
          <div className="form-section">
            <div className="form-row">
              <label htmlFor="name">
                신청자명 <span className="required">*</span>
              </label>
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
              <label>연락처 <span className="required">*</span></label>
              <div className="phone-input-wrapper">
                <select
                  id="phone1"
                  name="phone1"
                  value={formData.phone1}
                  onChange={handleChange}
                >
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
              <label>
                놀이터 유형 <span className="required">*</span>
              </label>
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
              <label htmlFor="animalNumber">
                반려견 수 <span className="required">*</span>
              </label>
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
              <label htmlFor="guardianNumber">보호자 수</label>
              <input
                type="number"
                id="guardianNumber"
                name="guardianNumber"
                value={formData.guardianNumber}
                onChange={handleChange}
                min={1}
              />
            </div>
          </div>

          
          {/* 시간대 선택 */}
          <div className="form-section">
          <div className="form-row">
            <label>
              시간대 선택 <span className="required">*</span>
            </label>

            <div className="time-slot-group">
              {displaySlots.map((slot) => {
                const allow = TYPE_LABELS[formData.landType] ?? [];
                // landType이 없으면 전부 disabled
                const enabledForType = formData.landType
                  ? allow.includes((slot.label || "").trim())
                  : false;
                const full = (slot.reservedCount ?? 0) >= (slot.capacity ?? 0);
                const disabled = full || !enabledForType;

                return (
                  <button
                    key={slot.timeSlotId}
                    type="button"
                    onClick={() => handleTimeSelect(slot.timeSlotId)}
                    disabled={disabled}
                    className={`time-slot-button ${
                      selectedSlotId === slot.timeSlotId ? "selected" : ""
                    }`}
                  >
                    {slot.label}
                    {(slot.capacity ?? 0) > 0 && (
                      <>
                        <br />정원: {slot.reservedCount ?? 0}/{slot.capacity}
                      </>
                    )}
                    {disabled && !full && " - 선택불가"}
                    {full && " - 마감"}
                  </button>
                );
              })}
            </div>
          </div>
        </div>

          {/* 비고 */}
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

          {/* 버튼 */}
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