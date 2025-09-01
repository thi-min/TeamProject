import React from "react";
import { Link } from "react-router-dom";
import FloatingChat from "../program/chat/services/FloatingChat";

const Footer = () => {
  return (
    <footer id="footer">
      <div className="wrap">
        <div className="top_box">
          <button type="button" className="top_btn">
            맨위로 이동하기 버튼
          </button>
          {/* 채팅 배너를 푸터에 추가 */}
          <FloatingChat />
        </div>
        <div className="nav_box">
          <ul className="nav_list">
            <li className="nav_item">
              <Link href="" className="nav_link">
                개인정보 처리방침
              </Link>
            </li>
            <li className="nav_item">
              <Link href="" className="nav_link">
                영상정보처리기기 운영관리 방침
              </Link>
            </li>
            <li className="nav_item">
              <Link href="" className="nav_link">
                저작권정책
              </Link>
            </li>
            <li className="nav_item">
              <Link href="" className="nav_link">
                이메일무단수집거부
              </Link>
            </li>
            <li className="nav_item">
              <Link href="" className="nav_link">
                오시는길
              </Link>
            </li>
          </ul>
        </div>
        <div className="info_box">
          <ul className="info_list">
            <li className="info_item">
              <address className="text">경기 동두천시 생연동 557-3</address>
            </li>
            <li className="info_item">
              <span className="text">TEL : 031-860-2062</span>
            </li>
            <li className="info_item">
              <span className="text">FAX : 031-860-2722</span>
            </li>
          </ul>
          <div className="refusal">
            본 사이트에 게시된 이메일주소는 자동수집을 거부하며, 이를 위반시
            정보통신방법에 의해 처벌될 수 있습니다.
          </div>
          <div className="copyright">
            COPYRIGHT © DONGDUCHEON CITY. ALL RIGHTS RESERVED.
          </div>
        </div>
        <div className="logo_box">
          <Link href="/www/index.do" className="logo_link">
            <span className="logo_text img">동두천시</span>
          </Link>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
