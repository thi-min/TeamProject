import axios from "axios";

const BASE_URL = "/api/reserve";

const LandReserveService = {
  /**
   * 놀이터 예약 생성 (POST /api/reserve)
   * @param {Object} fullReserveRequestDto - 통합 DTO
   * @returns {Promise}
   */
  createReserve: (fullReserveRequestDto) => {
    return axios.post(BASE_URL, fullReserveRequestDto);
  },

  /**
   * 놀이터 시간대 리스트 조회 (GET /api/timeslot?type=LAND)
   * @returns {Promise}
   */
  fetchTimeSlots: () => {
    return axios.get("/api/timeslot", {
      params: { type: "LAND" }
    });
  },

  /**
   * 날짜별 시간대 예약 현황 조회 (GET /api/land/count?date=yyyy-MM-dd)
   * @param {String} landDate
   * @returns {Promise}
   */
  fetchReservationStatus: (landDate) => {
    return axios.get("/api/land/count", {
      params: { date: landDate }
    });
  },
};

export default LandReserveService;