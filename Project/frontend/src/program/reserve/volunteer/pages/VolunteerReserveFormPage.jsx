import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "../../../../common/api/axios";
import VolunteerReserveService from "../services/VolunteerReserveService";
import "./../style/VolunteerReserveStyle.css";

const toDateStr = (d) =>
  typeof d === "string" ? d : new Date(d).toISOString().slice(0, 10);

const VolunteerReserveFormPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const memberNum = localStorage.getItem("memberNum");
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
    birth: "",
    reserveNumber: "",
    note: "",
    memberNum: null,
  });

  // membernum 주입
  useEffect(() => {
    if (memberNum) {
      setFormData((prev) => ({
        ...prev,
        memberNum: Number(memberNum),
      }));
    }
  }, []);

  /** 🔹 로그인 사용자 정보 불러오기 */
  useEffect(() => {
  const fetchMemberInfo = async () => {
    try {
      const token = localStorage.getItem("accessToken");
      if (!token) {
        alert("로그인이 필요합니다.");
        navigate("/login");
        return;
      }

      const res = await api.get("/member/mypage/memberdata", {
        headers: { Authorization: `Bearer ${token}` },
      });

      setFormData((prev) => ({
        ...prev,
        name: res.data.memberName ?? prev.memberName,
        phone: res.data.memberPhone ?? prev.memberPhone,
        birth: res.data.memberBirth ?? prev. memberBirth,
        memberNum: res.data.memberNum ?? prev.memberNum,
      }));
    } catch (err) {
      console.error("회원정보 불러오기 실패:", err);
      alert("회원정보를 불러올 수 없습니다. 다시 로그인해주세요.");
      navigate("/login");
    }
  };

  fetchMemberInfo();
}, [navigate]);

  /** VolunteerCountDto -> 표준형 변환 */
  const normalizeCountDto = (arr = []) =>
    arr.map((s) => {
      const full = (s.reservedCount ?? 0) >= (s.capacity ?? 0);
      return {
        timeSlotId: s.timeSlotId,
        label: s.label,
        capacity: s.capacity ?? 0,
        reservedCount: s.reservedCount ?? 0,
        enabled: true,
        disabled: full, 
      };
    });

  /** TimeSlotDto -> 표준형 변환 */
  const normalizeSlotDto = (arr = []) =>
    arr.map((s) => ({
      timeSlotId: s.timeSlotId,
      label: s.label,
      capacity: s.capacity ?? 0,
      reservedCount: s.reservedCount ?? 0,          
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

    if (!formData.memberNum) {
      try {
        const res2 = await VolunteerReserveService.fetchTimeSlots();
        const slotsData = normalizeSlotDto(res2.data);
        if (mounted) setDisplaySlots(slotsData);
      } catch (err) {
        console.error("시간대 기본 목록 API 실패:", err);
      }
      return;  // ✅ 여기서 종료 (아래 예약 현황 조회는 memberNum 있을 때만 실행)
    }

    try {
      setLoading(true);
      setErrorMsg("");

      let slotsData = null;

      // ✅ 예약 현황 API 먼저 호출
      try {
        const res = await VolunteerReserveService.fetchReservationStatus(
          selectedDate,
          formData.memberNum
        );
        if (mounted) {
          slotsData = normalizeCountDto(res.data); // 바로 slotsData에 넣음
          setDisplaySlots(slotsData);
          setLoading(false);
          return; // 성공하면 여기서 종료
        }
      } catch (err) {
        console.error("예약 현황 API 실패:", err);
      }

      // ✅ 예약 현황 없거나 실패 시 → 전체 시간대 불러오기
      if (!slotsData) {
        const res2 = await VolunteerReserveService.fetchTimeSlots();
        slotsData = normalizeSlotDto(res2.data); // Land랑 동일하게 normalizeSlotDto 사용
        if (mounted) setDisplaySlots(slotsData);
      }
    } catch (err) {
      console.error("시간대 목록 API 실패:", err);
      if (mounted) setErrorMsg("시간대 목록을 불러오지 못했습니다.");
    } finally {
      if (mounted) setLoading(false);
    }
  };

  loadSlots();
  return () => { mounted = false; };
}, [selectedDate, formData.memberNum]);

  /** 입력 핸들러 */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  /** 시간대 선택 */
  const handleTimeSelect = (slotId) => setSelectedSlotId(slotId);

  /** 제출 처리 */
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.reserveNumber) return alert("신청 인원 수를 선택해 주세요.");
    if (!selectedDate) return alert("예약 날짜를 선택해 주세요.");
    if (!selectedSlotId) return alert("시간대를 선택해 주세요.");

    // 선택한 시간대 db에 존재하는지
    const selectedSlot = displaySlots.find(s => s.timeSlotId === selectedSlotId);
    if (!selectedSlot) {
      return alert("선택한 시간대 정보를 불러올 수 없습니다.");
    }
    //정원 검사
    const total = (selectedSlot.reservedCount ?? 0) + Number(formData.reserveNumber ?? 0);
    if (total > (selectedSlot.capacity ?? 0)) {
      return alert(
        `선택한 인원이 남은 정원을 초과했습니다.\n` +
        `현재 신청 인원: ${selectedSlot.reservedCount ?? 0} / 최대 ${selectedSlot.capacity}`
      );
    }
    try {
    const { data: exists } = await api.get("/api/reserve/check-duplicate", {
      params: { memberNum: formData.memberNum, date: selectedDate, timeSlotId: selectedSlotId, type: "VOLUNTEER" },
      });
      if (exists) {
        return alert("이미 예약하신 시간대입니다. 다른 시간대를 선택해 주세요.");
      }
    } catch (err) {
      console.error("중복 검사 실패:", err);
      return alert("중복 검사 중 오류가 발생했습니다.");
    }
    navigate("/reserve/volunteer/confirm", {
      state: {
        formData,
        selectedDate,
        selectedSlotId,
        timeSlots: displaySlots,
      },
    });
  };

  if (loading)return <div className="volunteer-form-page">시간대를 불러오는 중입니다…</div>;
  if (errorMsg) return <div className="volunteer-form-page">{errorMsg}</div>;

  return (
    <div className="volunteer-form-page">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon vol"></div>
          <div className="form_title">봉사 예약 신청</div>
        </div>
      </div>
      <h3 className="form-title">봉사활동 예약 신청 폼</h3>
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
            <label>생년월일</label>
            <p>{formData.birth || "--"}</p>
          </div>
          <div className="form-row">
            <label htmlFor="reserveNumber">
              신청 인원 수 <span className="required">*</span>
            </label>
            <select
              name="reserveNumber"
              value={formData.reserveNumber}
              onChange={handleChange}
              required
            >
              <option value="">선택</option>
              {[...Array(10)].map((_, i) => (
                <option key={i + 1} value={i + 1}>
                  {i + 1}명
                </option>
              ))}
            </select>
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
                const full =
                  (slot.reservedCount ?? 0) >= (slot.capacity ?? 0);
                return (
                  <button
                    key={slot.timeSlotId}
                    type="button"
                    onClick={() => handleTimeSelect(slot.timeSlotId)}
                    disabled={full || !slot.enabled}
                    className={`time-slot-button ${
                      selectedSlotId === slot.timeSlotId ? "selected" : ""
                    }`}
                  >
                    {slot.label}
                    {(slot.capacity ?? 0) > 0 && (
                      <>
                        <br />
                        {`정원: ${slot.reservedCount ?? 0}/${slot.capacity}`}
                      </>
                    )}
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
        <div className="form_center_box">

          <div className="temp_btn white md">
            <button type="button" className="btn" onClick={() => window.history.back()}>
              이전
            </button>
          </div>

          <div className="temp_btn md">
            <button type="submit" className="btn" >
              다음
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default VolunteerReserveFormPage;