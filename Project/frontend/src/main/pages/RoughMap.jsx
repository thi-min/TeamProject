import React, { useEffect } from "react";

export default function RoughMap() {
  useEffect(() => {
    // daum 객체 준비될 때까지 대기
    const timer = setInterval(() => {
      if (window.daum?.roughmap?.Lander) {
        new window.daum.roughmap.Lander({
          timestamp: "1756631194028",
          key: "7xasvvnbqw8",
          mapWidth: "100%",   // 반응형
          mapHeight: "100%", // 원하는 높이
        }).render();
        clearInterval(timer); // 한 번만 실행
      }
    }, 100);
    return () => clearInterval(timer);
  }, []);

  return (
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
  );
}
