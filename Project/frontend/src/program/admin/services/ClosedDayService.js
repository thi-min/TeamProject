import api from "../../../common/api/axios";

const ClosedDayService = {
  // 휴무일 조회
  getClosedDays(year, month) {
    return api.get("/api/closed-days", {
      params: { year, month },
    });
  },

  // 휴무일 등록
  setClosedDay(closedDay) {
    return api.post("/api/closed-days", closedDay);
  },

  // 휴무일 삭제
  deleteClosedDay(date) {
    return api.delete(`/api/closed-days/${date}`);
  },

  // 특정 연도의 공휴일 자동 등록
  registerHolidays(year) {
    return api.post(`/api/closed-days/holidays/${year}`);
  },
};

export default ClosedDayService;