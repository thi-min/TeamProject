import React from "react";
import "../styles/contents2.css";
import smallDogImg from "../images/small-dog.png";
import largeDogImg from "../images/large-dog.png";
import convenientImg from "../images/convenient-facilities.png";

const LandInfoPage = () => {
  return (
    <div className="cts4_wrap">
        <h3>놀이터 소개</h3>
        <div class="box">
            <span class="box_title">놀이터 개요</span>
            <span class="box_desc">반려견과 보호자가 함께 즐길 수 있는 안전하고 쾌적한 공간입니다.
            <br />
            대형견과 중/소형견으로 구분된 놀이터와 편의 시설을 통해
            반려견의 사회성과 건강을 증진시켜 보세요.</span>
        </div>
        <div className="land-info-page">
        <div className="land-info-container">
            <div className="land-info-sections">
            <div className="land-info-card">
                <h3>소형견 놀이터</h3>
                <img
                src={smallDogImg}
                alt="소형견 놀이터"
                className="land-info-img"
                />
                <p>
                체구가 작은 반려견들이 안전하게 뛰어놀 수 있도록
                소형견 전용 공간을 제공합니다.
                </p>
            </div>

            <div className="land-info-card">
                <h3>대형견 놀이터</h3>
                <img
                src={largeDogImg}
                alt="대형견 놀이터"
                className="land-info-img"
                />
                <p>
                활발한 활동이 필요한 대형견들을 위한 넓은 운동장을 마련했습니다.
                </p>
            </div>

            <div className="land-info-card">
                <h3>편의 시설</h3>
                <img
                src={convenientImg}
                alt="편의 시설"
                className="land-info-img"
                />
                <p>
                보호자를 위한 휴식 공간, 음수대, 반려견 안전 관리 시설이
                구비되어 있습니다.
                </p>
            </div>
            </div>

            {/* 이용 요금 안내 표 */}
            <h3>이용 요금 안내</h3>
            <div className="pay-info-section">
            <table className="table type2 responsive border">
                <thead>
                <tr>
                    <th>구분</th>
                    <th>내용</th>
                    <th>금액</th>
                </tr>
                </thead>
                <tbody>
                    <tr>
                        <td rowSpan="2">기본 요금</td>
                        <td>반려견 1마리</td>
                        <td>2,000원</td>
                    </tr>
                    <tr>
                    
                    </tr>

                    <tr>
                        <td rowSpan="2">추가 요금</td>
                        <td>보호자 추가</td>
                        <td>명 당 1,000원</td>
                    </tr>
                    <tr>
                        <td>반려견 추가</td>
                        <td>마리 당 1,000원</td>
                    </tr>
                </tbody>
            </table><br></br>
            <p>※기본요금에 보호자 수는 포함되지 않습니다.</p>
            <p>※반려견 한마리당 보호자 수는 최대 2명까지 제한하고 있습니다.</p>
            </div>
        </div>
        </div>
    </div>
  );
};

export default LandInfoPage;