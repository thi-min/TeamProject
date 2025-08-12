import axios from "axios";

const API_BASE = process.env.REACT_APP_API_BASE || "";
const api = axios.create({ baseURL: API_BASE });
const TimeSlotService = {
  fetchLandTimeSlots,
  createTimeSlot,
  updateTimeSlot,
  deleteTimeSlot,
};


// LAND 타입 전체 슬롯 조회(사용자/관리 공용)
export const fetchLandTimeSlots = () =>
  api.get("/api/time-slots/LAND");

// 관리자 - 시간대 추가
export const createTimeSlot = (dto) =>
  api.post("/api/admin/time-slots", dto);

// 관리자 - 시간대 수정
export const updateTimeSlot = (id, dto) =>
  api.put(`/api/admin/time-slots/${id}`, dto);

// 관리자 - 시간대 삭제
export const deleteTimeSlot = (id) =>
  api.delete(`/api/admin/time-slots/${id}`);

export default TimeSlotService;