import axios from "axios";

// .env.development 에 REACT_APP_API_BASE=http://localhost:8080 넣고 개발서버 재시작 권장
const API_BASE = process.env.REACT_APP_API_BASE || "";
const api = axios.create({ baseURL: API_BASE });

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
fetchReservationStatus(landDate, memberNum, landType) {
  return api.get("/api/land/timeslots", {
    params: { date: landDate, memberNum, landType }
  });
},
fetchTimeSlots() {
  return api.get("/api/timeslots/LAND");
},

  // 예약 생성 (기존 유지)
  createReserve(fullReserveRequestDto) {
    return api.post("/api/reserve", fullReserveRequestDto);
  },
};

export default LandReserveService;