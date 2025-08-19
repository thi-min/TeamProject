import axios from "axios";

const API_BASE = process.env.REACT_APP_API_BASE || "";
const api = axios.create({ baseURL: API_BASE });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

/** 공통: LandCountDto/TimeSlotDto → 화면 표준 형태로 변환  */
const normalizeFromCountDto = (arr = []) =>
  arr.map((s) => ({
    timeSlotId: s.timeSlotId,
    label: s.label,
    capacity: s.capacity ?? 0,
    reservedCount: s.reservedCount ?? 0,
    enabled: s.enabled ?? true,
    landType: s.landType ?? null,
  }));

const normalizeFromSlotDto = (arr = []) =>
  arr.map((s) => ({
    timeSlotId: s.id ?? s.timeSlotId,
    label: s.label,
    capacity: s.capacity ?? 0,
    reservedCount: 0, // 기본 슬롯에는 집계가 없음
    enabled: s.enabled ?? true,
    landType: null,
  }));
/**
 * 백엔드 매핑
 * - GET /api/land/timeslots?date=yyyy-MM-dd&memberNum=1&landType=SMALL
 *     -> List<LandCountDto> (timeSlotId, label, landType, reservedCount, capacity)
 * - GET /api/timeslots/land
 *     -> List<TimeSlotDto> (id, label, startTime, endTime, capacity, enabled)
 * - POST /api/reserve
 *     -> 예약 생성 (사용 중이던 엔드포인트 유지)
 */
const LandReserveService = {
  // 날짜+유형별 시간대 현황(우선 사용)

  async getTimeSlotsWithCount(landDate, memberNum, landType) {
    try {
      const { data } = await api.get("/api/land/timeslots", {
        params: { date: landDate, memberNum, landType },
      });
      return normalizeFromCountDto(data);
    } catch (err) {
      // 집계 API 실패 시 기본 슬롯으로 폴백
      try {
        const { data } = await api.get("/api/timeslots/LAND");
        return normalizeFromSlotDto(data);
      } catch (fallbackErr) {
        // 최종 에러 그대로 던짐
        throw fallbackErr;
      }
    }
  },

  fetchReservationStatus(landDate, memberNum, landType) {
    return api.get("/api/land/timeslots", {
      params: { date: landDate, memberNum, landType }
    });
  },

  // 전체 LAND 타임슬롯
  fetchTimeSlots() {
    return api.get("/api/timeslots/LAND");
  },

  // 예약 생성 (기존 유지)
  createReserve(fullReserveRequestDto) {
    return api.post("/api/reserve", fullReserveRequestDto);
  },
};

export default LandReserveService;