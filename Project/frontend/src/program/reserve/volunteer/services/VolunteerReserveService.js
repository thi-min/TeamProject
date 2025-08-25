import api from "../../../../common/api/axios";

/**
 * 백엔드 매핑
 * - GET /api/volunteer/timeslots?date=yyyy-MM-dd&memberNum=1
 *     -> List<VolunteerCountDto> (timeSlotId, label, reservedCount, capacity)
 * - GET /api/timeslots/VOLUNTEER
 *     -> List<TimeSlotDto> (id, label, startTime, endTime, capacity, enabled)
 * - POST /api/reserve
 *     -> 예약 생성 (공통 엔드포인트 유지)
 */
const VolunteerReserveService = {
  // 날짜별 봉사 시간대 현황 조회
  fetchReservationStatus(volDate, memberNum) {
    return api.get("/api/volunteer/timeslots", {
      params: { date: volDate, memberNum }
    });
  },

  // 전체 시간대 조회 (봉사 전용)
  fetchTimeSlots() {
    return api.get("/api/timeslots/VOL");
  },

  // 예약 생성 (공통 엔드포인트)
  createReserve(fullReserveRequestDto) {
    return api.post("/api/reserve", fullReserveRequestDto);
  },
};

export default VolunteerReserveService;