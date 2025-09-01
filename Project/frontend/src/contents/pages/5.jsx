import React from "react";
import "../styles/contents2.css";

const VolunteerInfoPage = () => {
  return (
    <div className="cts5_wrap">
    <h3>봉사 활동 안내</h3>
        <div class="box">
            <span class="box_title">봉사 개요</span>
            <span class="box_desc">보호자와 반려견을 위한 다양한 봉사 프로그램에 참여해 보세요.
            <br />
            주말에만 운영되는 봉사활동을 통해 반려동물과 교감하며 뜻깊은 시간을 보내실 수 있습니다.</span>
        </div>
    <div className="volunteer-info-page">
      <div className="volunteer-info-container">
        <h3>봉사 프로그램</h3>
        <div className="volunteer-program-section">
          <div className="volunteer-program-cards">
            <div className="volunteer-program-card">
              <h4>유기견 보호소 청소</h4>
              <p>보호소 내·외부 환경을 정비하고 반려견들이 쾌적하게 생활할 수 있도록 도와주세요.</p>
            </div>
            <div className="volunteer-program-card">
              <h4>유기견과 산책</h4>
              <p>반려견과 함께 산책하며 교감의 시간을 보내고 사회성을 길러주세요.</p>
            </div>
            <div className="volunteer-program-card">
              <h4>유기견 목욕</h4>
              <p>반려견들의 위생과 건강을 위해 깨끗하게 씻겨주는 활동에 참여해 보세요.</p>
            </div>
          </div>
        </div>

        {/* 이용 안내 표 */}
        <h3>이용 안내</h3>
        <div className="volunteer-guide-section">
          <table className="table type2 responsive border">
            <thead>
              <tr>
                <th>구분</th>
                <th>내용</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>대상</td>
                <td>만 16세 이상 (미성년자는 보호자 동반 필수)</td>
              </tr>
              <tr>
                <td>활동 요일</td>
                <td>주말(토·일)</td>
              </tr>
              <tr>
                <td>시간대</td>
                <td>오전 09:00 ~ 12:00 / 오후 13:00 ~ 16:00</td>
              </tr>
              <tr>
                <td>예약 방법</td>
                <td>홈페이지 봉사 예약 메뉴를 통해 사전 예약 필수</td>
              </tr>
            </tbody>
          </table>
          <br />
          <p>※ 예약은 선착순으로 마감될 수 있습니다.</p>
          <p>※ 미성년자와 동반하는 보호자는 참가자 수에 적용되지 않습니다.</p>
          <p>※ 봉사활동 참여 시 편한 복장과 운동화를 착용해 주세요.</p>
        </div>
      </div>
      </div>
    </div>
  );
};

export default VolunteerInfoPage;