import React from "react";
import { Link } from "react-router-dom";

export default function Join() {
  return (
    <div className="p-wrap">
      <h3>
        사용자 준수사항 <span className="p-form__required">(필수)</span>
      </h3>
      <div className="p-agree">
        <div className="p-agree__condents">
          <div className="p-agree__inner">
            <h4 className="h0 p-agree__title">제 1 조 (목적)</h4>
            <p>
              ㅇㅇㅇ(기관명)이 제공하는 서비스를 이용하기 위해 필요한 최소한의
              개인정보를 수집하고 있습니다. 이에 개인정보의 수집 및 이용에
              관하여 아래와 같이 고지 하오니 충분히 읽어 보신 후 동의하여 주시기
              바랍니다.
            </p>
            <ul className="p-agree__list">
              <li>회원 가입 및 서비스 이용 시 본인의 확인</li>
              <li>ㅇㅇㅇ(기관명) 예약, 교육 수강 신청 게시판 글 작성</li>
            </ul>
            <div className="h0 p-agree__title">
              2. 수집·이용 하려는 개인정보의 항목
            </div>
            <ul className="p-agree__list">
              <li>
                필수 항목 : 이름, 비밀번호, 휴대전화
                <ul>
                  <li>
                    휴대전화 : 예약 및 교육 수강 신청 관련 내용 알림 및 확인
                  </li>
                </ul>
              </li>
              <li>선택 항목 : 자택전화번호</li>
              <li>홈페이지 접속 정보 및 서비스 이용 정보</li>
            </ul>
            <div className="h0 p-agree__title">3. 수집 방법</div>
            <ul className="p-agree__list">
              <li>
                홈페이지 예약, 수강신청 및 글 작성 란에 마련 된 개인정보
                입력란에 회원 본인이 직접 입력하는 방식
              </li>
            </ul>
            <div className="h0">4. 보유 및 이용 기간</div>
            <ul className="p-agree__list">
              <li>
                ㅇㅇㅇ(기관명) 홈페이지 회원정보 보유기간은 2년이며 이용자가
                회원 탈퇴를 요청하거나 개인정보의 수집·이용 등에 대한 동의를
                철회 요청이 있는 경우 해당 개인정보를 즉시 파기 합니다.
              </li>
            </ul>
            <div className="h0 p-agree__title">
              5. 동의거부권 및 동의 거부에 따른 불이익
            </div>
            <ul className="p-agree__list">
              <li>
                회원 가입자는 개인정보 수집·이용에 대하여 거부할 수 있는 권리가
                있습니다 단 이에 대한 동의를 거부할 경우에는 예약, 수강신청 및
                특정 게시판 글 작성은 불가하며 그 밖에 개인정보 수집, 이용하여
                이루어지는 서비스 이외의 홈페이지 이용에는 문제가 없습니다.
              </li>
              <li>
                좀 더 상세한 개인정보파일 등록사항 공개는 행정안전부
                개인정보보호 종합지원포털(www.privacy.go.kr) → 개인정보민원 →
                개인정보의 열람 등 요구 → 개인정보파일 목록 검색 메뉴를 활용 해
                주시기 바랍니다.
              </li>
              <li>
                선택 정보를 입력하지 않은 경우에도 서비스 이용 제한은 없으며
                민감한 개인정보(주민등록번호, 인종, 사상 및 범죄 및 의료기록
                등)는 수집하지 않습니다.
              </li>
            </ul>
          </div>
        </div>
        <div className="margin_t_5">
          <span className="p-form-radio">
            <input
              type="radio"
              name="aa"
              id="agree"
              className="p-form-radio__input"
            />
            <label htmlFor="agree" className="p-form-radio__label margin_r_20">
              동의 합니다.
            </label>
          </span>
          <span className="p-form-radio">
            <input
              type="radio"
              name="aa"
              id="agree-2"
              className="p-form-radio__input"
            />
            <label htmlFor="agree-2" className="p-form-radio__label">
              동의하지 않습니다.
            </label>
          </span>
        </div>
      </div>
      <h3>
        개인정보 수집 및 이용안내
        <span className="p-form__required">(선택)</span>
      </h3>
      <div className="p-agree">
        <div className="p-agree__condents">
          <div className="p-agree__inner">
            <div className="p-agree__title">1. 수집 및 이용 목적</div>
            <ul className="p-agree__list">
              <li>회원 가입 및 서비스 이용 시 본인의 확인</li>
              <li>ㅇㅇㅇ(기관명) 예약, 교육 수강 신청 게시판 글 작성</li>
            </ul>
          </div>
          <div className="margin_t_5">
            <span className="p-form-radio">
              <input
                type="radio"
                name="bb"
                id="agree2"
                className="p-form-radio__input"
              />
              <label
                htmlFor="agree2"
                className="p-form-radio__label margin_r_20"
              >
                동의 합니다.
              </label>
            </span>
            <span className="p-form-radio">
              <input
                type="radio"
                name="bb"
                id="agree2-1"
                className="p-form-radio__input"
              />
              <label htmlFor="agree2-1" className="p-form-radio__label">
                동의하지 않습니다.
              </label>
            </span>
          </div>
        </div>
      </div>
      <div className="form_center_box">
        <div className="temp_btn white md">
          <Link to="/" className="btn">
            취소
          </Link>
        </div>
        <div className="temp_btn md">
          <Link to="/phonetest" className="btn">
            다음
          </Link>
        </div>
      </div>
    </div>
  );
}
