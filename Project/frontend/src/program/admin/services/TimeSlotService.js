import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE || "",
});

// 관리(ADMIN)
export const createTimeSlot = (dto) =>
  api.post("/api/admin/timeslots", dto);

export const updateTimeSlot = (id, dto) =>
  api.put(`/api/admin/timeslots/${id}`, dto);

export const deleteTimeSlot = (id) =>
  api.delete(`/api/admin/timeslots/${id}`);

// 조회(공용)
export const fetchTimeSlotsByType = (timeType) =>
  api.get(`/api/admin/timeslots/${timeType}`);