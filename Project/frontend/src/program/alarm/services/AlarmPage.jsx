import { useEffect, useState } from "react";
import { api } from "../../../common/api/axios.js";
import { useNavigate } from "react-router-dom";

const AlarmPage = ({ showBanner, setShowBanner }) => {
  const [bannerMessage, setBannerMessage] = useState("");
  const navigate = useNavigate();

  const getMemberNumFromToken = () => {
    const token = localStorage.getItem("accessToken");
    if (!token) return null;
    try {
      return JSON.parse(atob(token.split(".")[1])).memberNum;
    } catch (e) {
      console.error("JWT 파싱 실패:", e);
      return null;
    }
  };

  // 로그인한 회원 예약 상태 확인
  useEffect(() => {
    const fetchReservationStatus = async () => {
      const memberNum = getMemberNumFromToken();
      if (!memberNum) return;

      try {
        const response = await api.get(`/api/reserve/my?memberNum=${memberNum}`);
        const hasChanged = response.data.some(
          r => r.reserve_state === "DONE" || r.reserve_state === "REJ"
        );
        if (hasChanged) {
          setBannerMessage("예약이 변경되었습니다");
          setShowBanner(true);
        }
      } catch (e) {
        console.error("예약 상태 조회 실패:", e);
      }
    };

    fetchReservationStatus();
  }, []);

  if (!showBanner) return null;

  return (
    <div
      className="alarm-banner"
      onClick={() => {
        setShowBanner(false);
        navigate("/member/mypage/reserves");
      }}
    >
      {bannerMessage}
    </div>
  );
};

export default AlarmPage;