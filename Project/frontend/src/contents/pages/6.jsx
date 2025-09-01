// 📁 src/contents/1.jsx
import React from "react";

export default function Content1() {
  return (
    <div className="cts1_wrap">
      <h3>센터 인사말</h3>

      <div className="ct-section">
        <div className="ct-split">
          <div className="ct-split-col">
            <p className="ct-p">
              함께마당은 반려동물과 사람이 <strong>안전하고 즐겁게</strong>{" "}
              어울릴 수 있는 지역 커뮤니티 공간입니다. 유기·유실 동물 보호, 입양
              지원, 봉사활동 프로그램과 놀이터 운영을 통해{" "}
              <strong>지속가능한 반려문화</strong>를 만들어가고 있습니다.
            </p>
            <p className="ct-p">
              작은 실천이 모여 큰 변화를 만든다고 믿습니다. 방문하시는 모든
              분들이
              <strong> 편안하고 따뜻한 경험</strong>을 하실 수 있도록 최선을
              다하겠습니다.
            </p>

            <div className="ct-quote">
              <span className="ct-quote-mark">“</span>
              <p>
                반려의 시작과 끝에 <strong>존중과 책임</strong>이 함께하도록,
                함께마당이 동행하겠습니다.
              </p>
            </div>

            <div className="ct-sign">
              <p className="ct-sign-name">함께마당 직원 일동</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
