import axios from "axios";

const BASE_URL = "/api/reserve";

const VolunteerReserveService = {
  /**
   * 봉사 예약 생성 (POST /api/reserve)
   * @param {Object} fullReserveRequestDto
   * @returns {Promise}
   */
  createReserve: (fullReserveRequestDto) => {
    return axios.post(BASE_URL, fullReserveRequestDto);
  },

  /**
   * 봉사 시간대 리스트 조회 (GET /api/timeslot?type=VOLUNTEER)
   * @returns {Promise}
   */
  fetchTimeSlots: () => {
    return axios.get("/api/timeslot", {
      params: { type: "VOLUNTEER" }
    });
  },

  /**
   * 날짜별 봉사 예약 현황 조회 (GET /api/volunteer/count?date=yyyy-MM-dd)
   * @param {String} volDate
   * @returns {Promise}
   */
  fetchReservationStatus: (volDate) => {
    return axios.get("/api/volunteer/count", {
      params: { date: volDate }
    });
  },
};

export default VolunteerReserveService;