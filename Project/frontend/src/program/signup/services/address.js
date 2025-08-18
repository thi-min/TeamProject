import axios from "axios";

export async function searchAddress(keyword, page = 1, size = 10) {
  const res = await axios.get("/api/addresses/search", {
    params: { keyword, page, size },
  });

  return res.data;
}
