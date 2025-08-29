import api from "../../../common/api/axios";

const TimeSlotService = {
  // 관리(ADMIN)
  create(dto) {
    return api.post("/api/admin/timeslots", dto);
  },

  update(id, dto) {
    return api.put(`/api/admin/timeslots/${id}`, dto);
  },

  delete(id) {
    return api.delete(`/api/admin/timeslots/${id}`);
  },

  // 조회(공용)
  fetchByType(timeType) {
    return api.get(`/api/admin/timeslots/${timeType}`);
  },
};

export default TimeSlotService;
