import api from "../../common/api"; // axios 기본 설정 불러오기

const AdminReserveService = {
  // 놀이터 예약 목록
  getLandReservations() {
    return api.get("/api/admin/reserve/land");
  },

  // 봉사 예약 목록
  getVolunteerReservations() {
    return api.get("/api/admin/reserve/volunteer");
  },

  // 놀이터 예약 검색
  searchLandReservations(filters) {
    return api.post("/api/admin/reserve/land/search", filters);
  },

  // 봉사 예약 검색
  searchVolunteerReservations(filters) {
    return api.post("/api/admin/reserve/volunteer/search", filters);
  },
};

export default AdminReserveService;