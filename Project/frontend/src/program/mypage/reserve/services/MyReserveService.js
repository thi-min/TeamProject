import axios from "axios";

const BASE_URL = "/api/reserve";

const MyReserveService = {
  getMyReserves: async () => {
    const res = await axios.get(`${BASE_URL}/my`);
    return res.data;
  },

  getMyReservesByType: async (type) => {
    const res = await axios.get(`${BASE_URL}/my/type`, {
      params: { type },
    });
    return res.data;
  }
};

export default MyReserveService;