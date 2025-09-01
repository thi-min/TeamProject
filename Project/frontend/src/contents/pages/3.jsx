// 📁 src/contents/1.jsx
import React, { useEffect } from "react";

export default function Content3() {
  useEffect(() => {
    // daum 객체 준비될 때까지 대기
    const timer = setInterval(() => {
      if (window.daum?.roughmap?.Lander) {
        new window.daum.roughmap.Lander({
          timestamp: "1756631194028",
          key: "7xasvvnbqw8",
          mapWidth: "100%", // 반응형
          mapHeight: "100%", // 원하는 높이
        }).render();
        clearInterval(timer); // 한 번만 실행
      }
    }, 100);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="cts3_wrap map_content_box">
      <h3>오시는길</h3>
      <div className="map_box">
        <div className="con_map_wrap">
          <div className="map_inner main_map_size">
            <div
              id="daumRoughmapContainer1756631194028"
              className="root_daum_roughmap root_daum_roughmap_landing"
            ></div>
          </div>
        </div>
      </div>
      <div className="to_info_box">
        <div className="info_item">
          <h4 className="h4_icon">
            <i className="h4_icon type1"></i>주소
          </h4>
          <div className="box">
            <ul className="bu">
              <li>대전광역시 유성구 가정로 218</li>
            </ul>
          </div>
        </div>
        <div className="info_item">
          <h4 className="h4_icon">
            <i className="h4_icon type2"></i>전화번호
          </h4>
          <div className="box">
            <ul className="bu">
              <li>042-111-2222</li>
            </ul>
          </div>
        </div>
      </div>
      <h4 className="h4_icon">
        <i className="h4_icon type4"></i>버스로 오시는길
      </h4>
      <div className="box">
        <ul className="bu">
          <li>102번: 대전역 ↔ 유성온천 ↔ KAIST</li>
          <li>103번: 서대전역 ↔ 유성온천 ↔ 한밭대학교</li>
          <li>704번: 대전복합터미널 ↔ 유성온천 ↔ 현충원</li>
          <li>911번: 대전역 ↔ 유성구청 ↔ 반석동</li>
          <li>유성온천역 정류장 또는 가정로 정류장</li>
        </ul>
      </div>
      <h4 className="h4_icon">
        <i className="h4_icon type5"></i>지하철로 오시는길
      </h4>
      <div className="box">
        <ul className="bu">
          <li>대전역, 중앙로역, 서대전네거리역 등에서 1호선 탑승</li>
          <li>반석 방면 열차 이용</li>
          <li>유성온천역 하차 (3번 출구)</li>
          <li>도보 15분</li>
        </ul>
      </div>
      <h4 className="h4_icon">
        <i className="h4_icon type6"></i>자가용으로 오시는길
      </h4>
      <div className="box">
        <ul className="bu">
          <li>
            경부고속도로
            <ul className="bu">
              <li>대전IC 진입 → 대전시내 방향</li>
              <li>유성대로 → 가정로 방향</li>
            </ul>
          </li>
          <li>
            호남고속도로
            <ul className="bu">
              <li>유성IC 진입 (가장 가까움)</li>
              <li>32번 국도 → 가정로 방향</li>
            </ul>
          </li>
          <li>
            대전남부순환고속도로
            <ul className="bu">
              <li>유성JC → 가정로 방향</li>
            </ul>
          </li>
          <li>
            주차안내
            <ul className="bu">
              <li>무료 주차장 완비 (대형견 동반 고객용 여유 공간)</li>
              <li>주차 수용대수: 30대</li>
              <li>대형차량 주차 불가</li>
            </ul>
          </li>
        </ul>
      </div>
    </div>
  );
}
