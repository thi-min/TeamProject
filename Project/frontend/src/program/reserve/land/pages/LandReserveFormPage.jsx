import React, { useEffect, useState, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import LandReserveService from "../services/LandReserveService";
import "./../style/LandReserveStyle.css";

const toDateStr = (d) =>
  typeof d === "string" ? d : new Date(d).toISOString().slice(0, 10);

const LandReserveFormPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const selectedDate = location.state?.selectedDate
    ? toDateStr(location.state.selectedDate)
    : "";

  const [displaySlots, setDisplaySlots] = useState([]);
  const [selectedSlotId, setSelectedSlotId] = useState(null);
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const [formData, setFormData] = useState({
    name: "",
    phone: "",
    landType: "",
    animalNumber: "",
    reserveNumber: "",
    note: "",
    memberNum: null,
  });

  // 🔹 로그인 사용자 정보 불러오기
  useEffect(() => {
    const fetchMemberInfo = async () => {
      try {
        const token = localStorage.getItem("accessToken");
        if (!token) {
          alert("로그인이 필요합니다.");
          navigate("/login");
          return;
        }

        const res = await axios.get("/auth/mypage", {
          headers: { Authorization: `Bearer ${token}` },
        });

        setFormData((prev) => ({
          ...prev,
          name: res.data.memberName,
          phone: res.data.memberPhone,
          memberNum: res.data.memberNum,
        }));
      } catch (err) {
        console.error("회원정보 불러오기 실패:", err);
        alert("회원정보를 불러올 수 없습니다. 다시 로그인해주세요.");
        navigate("/login");
      }
    };

    fetchMemberInfo();
  }, [navigate]);

  /** LandCountDto -> 표준형 변환 */
  const normalizeCountDto = (arr = []) =>
    arr.map((s) => ({
      timeSlotId: s.timeSlotId,
      label: s.label,
      capacity: s.capacity ?? 0,
      reservedCount: s.reservedCount ?? 0,
      enabled: true,
    }));

  /** TimeSlotDto -> 표준형 변환 */
  const normalizeSlotDto = (arr = []) =>
    arr.map((s) => ({
      timeSlotId: s.timeSlotId,
      label: s.label,
      capacity: s.capacity ?? 0,
      reservedCount: 0,
      enabled: s.enabled ?? true,
      type: s.type,
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

        let slotsData = null;

        // 예약 현황 api
        if (formData.landType && formData.memberNum) {
          try {
            const res = await LandReserveService.fetchReservationStatus(
              selectedDate,
              formData.memberNum,
              formData.landType
            );
            if (mounted) {
              setDisplaySlots(normalizeCountDto(res.data));
              setLoading(false);
              return;
            }
          } catch (err) {
            console.error("예약 현황 API 실패:", err);
          }
        }

        // 전체 시간대 불러오기
        if (!slotsData) {
        const res2 = await LandReserveService.fetchTimeSlots();
        slotsData = normalizeSlotDto(res2.data);
      }

      // ✅ localStorage 규칙 적용
      const saved = localStorage.getItem("landRules");
      if (saved) {
        const rules = JSON.parse(saved);
        slotsData = slotsData.map(s => ({
          ...s,
          allowSmall: rules.SMALL.includes(s.timeSlotId),
          allowLarge: rules.LARGE.includes(s.timeSlotId),

          disabled:
          (formData.landType === "SMALL" && !rules.SMALL.includes(s.id)) ||
          (formData.landType === "LARGE" && !rules.LARGE.includes(s.id)),
          
        }));
      }

      if (mounted) setDisplaySlots(slotsData);

    } catch (err) {
      console.error("시간대 목록 API 실패:", err);
      if (mounted) setErrorMsg("시간대 목록을 불러오지 못했습니다.");
    } finally {
      if (mounted) setLoading(false);
    }
  };

  loadSlots();
  return () => {
    mounted = false;
  };
}, [selectedDate, formData.landType, formData.memberNum]);

  /** 입력 변경 핸들러 */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (name === "landType") {
      setSelectedSlotId(null); // 타입 변경 시 선택 초기화
    }
  };

  /** 시간대 선택 */
  const handleTimeSelect = (slotId) => setSelectedSlotId(slotId);

  /** 제출 처리 */
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.landType) return alert("놀이터 유형을 선택해 주세요.");
    if (!formData.animalNumber) return alert("반려견 수를 입력해 주세요.");
    if (!selectedDate) return alert("예약 날짜를 선택해 주세요.");
    if (!selectedSlotId) return alert("시간대를 선택해 주세요.");

    navigate("/reserve/land/confirm", {
      state: {
        formData,
        selectedDate,
        selectedSlotId,
        timeSlots: displaySlots,
      },
    });
  };

  /** 선택한 유형의 시간대만 필터링 */
  const filteredSlots = useMemo(() => {
    console.log("규칙:", localStorage.getItem("landRules"));
  console.log("displaySlots:", displaySlots);
    if (!formData.landType) {
      // 유형 선택 전 → 전체 시간대 보이되 선택 불가
      return displaySlots.map(slot => ({
        ...slot,
        disabled: true
      }));
    }

    return displaySlots.map(slot => {
      const full = (slot.reservedCount ?? 0) >= (slot.capacity ?? 0);

      // localStorage 규칙 기반 허용 여부 확인
      const rules = JSON.parse(localStorage.getItem("landRules") || "{}");
      const allowSmall = rules.SMALL?.includes(slot.timeSlotId) ?? true;
      const allowLarge = rules.LARGE?.includes(slot.timeSlotId) ?? true;

      let disabled = full || !slot.enabled;

      if (formData.landType === "SMALL" && !allowSmall) {
        disabled = true;
      }
      if (formData.landType === "LARGE" && !allowLarge) {
        disabled = true;
      }

      return {
        ...slot,
        disabled
      };
    });
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
        <p className="selected-date">
          선택한 날짜: <strong>{selectedDate || "-"}</strong>
        </p>

        {/* 신청자 정보 */}
        <div className="form-section">
          <div className="form-row">
            <label>신청자명</label>
            <p>{formData.name || "-"}</p>
          </div>
          <div className="form-row">
            <label>연락처</label>
            <p>{formData.phone || "--"}</p>
          </div>
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
          <div className="form-row">
            <label htmlFor="reserveNumber">보호자 수 <span className="required">*</span></label>
            <input
              type="number"
              id="reserveNumber"
              name="reserveNumber"
              value={formData.reserveNumber}
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
              {filteredSlots.map((slot) => {
                const full = (slot.reservedCount ?? 0) >= (slot.capacity ?? 0);
                const disabled = full || !slot.enabled;

                return (
                  <button
                    key={slot.timeSlotId}
                    type="button"
                    onClick={() => handleTimeSelect(slot.timeSlotId)}
                    disabled={slot.disabled || (slot.reservedCount ?? 0) >= (slot.capacity ?? 0)}
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
                    {slot.disabled && ""}
                    {(slot.reservedCount ?? 0) >= (slot.capacity ?? 0) && " - 마감"}
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
          <button
            className="prev-button"
            type="button"
            onClick={() => window.history.back()}
          >
            이전
          </button>
          <button className="next-button" type="submit">
            다음
          </button>
        </div>
      </form>
    </div>
  );
};

export default LandReserveFormPage;