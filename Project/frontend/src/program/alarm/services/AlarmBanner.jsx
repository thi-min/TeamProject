import { useEffect, useState } from "react";
import { api } from "../../../common/api/axios.js";
import { useNavigate } from "react-router-dom";
import "../style/Alarm.css";

// AlarmBanner 컴포넌트는 로그인한 사용자의 최근 알림을 가져와 표시합니다.
const AlarmBanner = ({ isOpen, onClose }) => {
    // 알림 목록 상태
    const [alarms, setAlarms] = useState([]);
    // 로딩 상태 관리
    const [loading, setLoading] = useState(false);
    // 페이지 이동을 위한 훅
    const navigate = useNavigate();

    // useEffect: isOpen이 바뀔 때마다 실행, 알람을 가져옵니다.
    useEffect(() => {
        // 배너가 열리지 않았으면 실행하지 않음
        if (!isOpen) return;

        // 비동기 함수: 알림 가져오기
        const fetchAlarms = async () => {
            setLoading(true);

            // 로컬스토리지에서 액세스 토큰 가져오기
            const token = localStorage.getItem("accessToken");

            // 토큰이 없으면 로그인 필요 메시지
            if (!token) {
                setAlarms([{ message: "로그인이 필요합니다.", date: "" }]);
                setLoading(false);
                return;
            }

            try {
                // API 호출, 백엔드에서 인증 처리
                const response = await api.get("/api/alarm/list", {
                    headers: { Authorization: `Bearer ${token}` }
                });

                if (response.data && response.data.length > 0) {
                    // 알림 데이터를 보기 좋게 가공
                    const formatted = response.data.map(a => ({
                        message: a.message,
                        date: a.lastUpdateTime ? new Date(a.lastUpdateTime).toLocaleDateString() : ""
                    }));
                    setAlarms(formatted); // 여러 개 알림 설정 가능
                } else {
                    // 알림이 없는 경우
                    setAlarms([{ message: "알림이 없습니다.", date: "" }]);
                }
            } catch (e) {
                console.error("알람 조회 실패:", e);
                if (e.response && e.response.status === 401) {
                    setAlarms([{ message: "로그인 세션이 만료되었습니다. 다시 로그인해주세요.", date: "" }]);
                } else {
                    setAlarms([{ message: "알람 로딩 실패 😢", date: "" }]);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchAlarms();
    }, [isOpen]); // isOpen이 바뀔 때마다 실행

    // 배너가 열려있지 않으면 아무것도 렌더링하지 않음
    if (!isOpen) return null;

    // 알림 항목 클릭 시 처리
    const handleClickItem = () => {
        onClose(); // 배너 닫기
        navigate("/member/mypage/reserves"); // 예약 페이지로 이동
    };

    return (
        <div className="alarm-dropdown">
            {/* 우측 상단 X 버튼 */}
            <button className="close-btn-x" onClick={onClose}>×</button>

            {/* 로딩 중일 때 표시, 아니면 알림 목록 표시 */}
            {loading ? (
                <div className="alarm-item">로딩 중...</div>
            ) : (
                alarms.map((alarm, idx) => (
                    <div key={idx} className="alarm-item" onClick={handleClickItem}>
                        {alarm.message} {alarm.date ? `(${alarm.date})` : ""}
                    </div>
                ))
            )}
        </div>
    );
};

export default AlarmBanner;
