import React from 'react';

import { Link } from 'react-router-dom'; // 페이지 이동용

const Footer = () => {
    return(
        <footer id="footer">
            <div class="wrap">
                <div class="top_box">
                    <button type="button" class="top_btn">맨위로 이동하기 버튼</button>
                </div>
                <div class="nav_box">
                    <ul class="nav_list">
                        <li class="nav_item">
                            <Link href="" class="nav_link">개인정보 처리방침</Link>
                        </li>
                        <li class="nav_item">
                            <Link href="" class="nav_link">영상정보처리기기 운영관리 방침</Link>
                        </li>
                        <li class="nav_item">
                            <Link href="" class="nav_link">저작권정책</Link>
                        </li>
                        <li class="nav_item">
                            <Link href="" class="nav_link">이메일무단수집거부</Link>
                        </li>
                        <li class="nav_item">
                            <Link href="" class="nav_link">오시는길</Link>
                        </li>
                    </ul>
                </div>
                <div class="info_box">
                    <ul class="info_list">
                        <li class="info_item">
                            <address class="text">경기 동두천시 생연동 557-3</address>
                        </li>
                        <li class="info_item">
                            <span class="text">TEL : 031-860-2062</span>
                        </li>
                        <li class="info_item">
                            <span class="text">FAX : 031-860-2722</span>
                        </li>
                    </ul>
                    <div class="refusal">본 사이트에 게시된 이메일주소는 자동수집을 거부하며, 이를 위반시 정보통신방법에 의해 처벌될 수 있습니다.</div>
                    <div class="copyright">COPYRIGHT © DONGDUCHEON CITY. ALL RIGHTS RESERVED.</div>
                </div>
                <div class="logo_box">
                    <Link href="/www/index.do" class="logo_link">
                        <span class="logo_text img">동두천시</span>
                    </Link>
                </div>
            </div>
        </footer>
    );
};

export default Footer;