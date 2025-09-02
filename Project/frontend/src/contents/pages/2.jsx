import React from "react";
import "../styles/contents2.css";
import centerMap from "../images/map.jpg";
export default function CenterIntroPage() {
  return (
    <div className="cts2_wrap">
      <h3>시설 소개</h3>
      <div className="ct-section">
        <div className="ct-map">  
        <div className="full_img_box">
        <img
              src={centerMap}
              alt="시설 지도"
              className="center-map"
            /> 
          </div>
        </div>
      </div>

      {/* 운영 정책 */}
      <div className="ct-section">
        <h3>운영 정책</h3>
        <table className="table type2 responsive border">
        <tbody>
          <tr>
            <th>이용 기준</th>
            <td>모든 반려견은 예방접종 확인 후 입장 가능합니다.</td>
          </tr>
          <tr>
            <th>안전 관리</th>
            <td>센터 내 관리자 상주, 사고 발생 시 즉시 대응 체계 운영.</td>
          </tr>
          <tr>
            <th>위생 규정</th>
            <td>놀이 후 세척실 이용 권장, 보호자 배변 처리 필수.</td>
          </tr>
          <tr>
            <th>예약 정책</th>
            <td>모든 놀이터와 프로그램은 온라인 사전 예약제로 운영됩니다.</td>
          </tr>
        </tbody>
      </table>
      </div>
    </div>
  );
}