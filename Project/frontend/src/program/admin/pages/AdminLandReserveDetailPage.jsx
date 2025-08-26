import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../../common/api/axios";
import "../style/ReserveManage.css";

const AdminLandReserveDetailPage = () => {
  const { reserveCode } = useParams();
  const navigate = useNavigate();
  const [detail, setDetail] = useState(null);
  const [newState, setNewState] = useState("");

  useEffect(() => {
    async function fetchDetail() {
      try {
        const { data } = await api.get(`/api/admin/reserve/land/${reserveCode}`);
        setDetail(data);
        setNewState(data.reserveState);
      } catch (err) {
        console.error("상세조회 실패:", err);
        alert("상세 정보를 불러오지 못했습니다.");
      }
    }
    fetchDetail();
  }, [reserveCode]);

  const handleUpdateState = async () => {
    if (!window.confirm("예약 상태를 변경하시겠습니까?")) return;
    try {
      await api.patch(`/api/admin/reserve/${reserveCode}/state`, { reserveState: newState, });
      alert("상태가 변경되었습니다.");
      navigate("/admin/reserve/land");
    } catch (err) {
      console.error("상태 변경 실패:", err);
      alert("상태 변경 중 오류가 발생했습니다.");
    }
  };

  if (!detail) return <p>로딩 중...</p>;

  return (
    <div className="admin-reserve-detail">
      <h2>놀이터 예약 상세</h2>
      <table className="table type2 responsive border">
        <colgroup>
          <col className="w20p" />
          <col />
        </colgroup>
        <tbody>
          <tr><th>예약 코드</th><td>{detail.reserveCode}</td></tr>
          <tr><th>회원명</th><td>{detail.memberName}</td></tr>
          <tr><th>연락처</th><td>{detail.memberPhone}</td></tr>
          <tr><th>예약일</th><td>{detail.landDate}</td></tr>
          <tr><th>시간대</th><td>{detail.label}</td></tr>
          <tr><th>반려견 수</th><td>{detail.animalNumber}</td></tr>
          <tr><th>보호자 수</th><td>{detail.reserveNumber}</td></tr>
          <tr><th>비고</th><td>{detail.note || "-"}</td></tr>
          <tr>
            <th>현재 상태</th>
            <td>
                <select className="ui-select" value={newState} onChange={(e) => setNewState(e.target.value)}>
                <option value="ING">진행중(ING)</option>
                <option value="DONE">완료(DONE)</option>
                <option value="REJ">거절(REJ)</option>
                <option value="CANCEL">취소(CANCEL)</option>
                </select>
            </td>
          </tr>
        </tbody>
      </table>

      <h2>결제 정보</h2>
      <table className="table type2 responsive border">
        <colgroup>
          <col className="w20p" />
          <col />
        </colgroup>
        <tbody>
          <tr><th>기본</th><td>{detail.basePriceDetail}</td></tr>
          <tr><th>추가</th><td>{detail.extraPriceDetail}</td></tr>
          <tr><th>총 결제금액</th><td><b>{detail.totalPrice}원</b></td></tr>
        </tbody>
      </table>

      <div className="form_center_box">
          <div className="temp_btn white md">
            <button type="button" className="btn" onClick={() => navigate(-1)}>
              목록보기
            </button>
          </div>

          <div className="temp_btn md">
            <button type="submit" className="btn" onClick={handleUpdateState} >
              상태변경 
            </button>
          </div>
      </div>
    </div>
  );
};

export default AdminLandReserveDetailPage;