import axios from "axios";

const BASE_URL = "/api/reserve";

const MyReserveService = {
  // 전체 예약 목록 조회
  getMyReserves: async (memberNum) => {
    const res = await axios.get(`${BASE_URL}/my`, {
      params: { memberNum },
    });
    return res.data;
  },

  // 유형별 예약 목록 조회 (type: 1 = LAND, 2 = VOLUNTEER)
  getMyReservesByType: async (memberNum, type) => {
    const res = await axios.get(`${BASE_URL}/my/type`, {
      params: { memberNum, type },
    });
    return res.data;
  },

  // ✅ 예약 상세 조회 (놀이터)
 getLandReserveDetail: async (reserveCode, token) => {
  const res = await axios.get(`${BASE_URL}/land/${reserveCode}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res.data;
},

  // ✅ 예약 상세 조회 (봉사)
  getVolunteerReserveDetail: async (reserveCode, token) => {
    const res = await axios.get(`${BASE_URL}/volunteer/${reserveCode}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return res.data;
},

  // ✅ 예약 취소
  cancelReserve: async (reserveCode, token) => {
    const res = await axios.delete(`${BASE_URL}/${reserveCode}/cancel`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    });
    return res.data;
  },
};

export default MyReserveService;