import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE || "",
});

// 관리(ADMIN)
export const createTimeSlot = (dto) =>
  api.post("/api/admin/time-slots", dto);

export const updateTimeSlot = (id, dto) =>
  api.put(`/api/admin/time-slots/${id}`, dto);

export const deleteTimeSlot = (id) =>
  api.delete(`/api/admin/time-slots/${id}`);

// 조회(공용)
export const fetchLandTimeSlots = () =>
  api.get("/api/time-slots/LAND");