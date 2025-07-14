import '../../../template/css/default.css';
import '../../../template/css/font.css';
import '../../../common/common.css';
import './main/css/main.css';

import logo from './image/main/dp_logo.png';

function MainLayout() {
  return (
    <div id="wrapper" className="page">
        <header id="header">
            <div className="header_top_box">
                <div className="top_link_list">
                    <div className="link_item">
                        <a href="#">로그인</a>
                    </div>
                    <div className="link_item">
                        <a href="#">회원가입</a>
                    </div>
                </div>
            </div>
            <div className="header_inner">
                <h1 className="logo">
                    <a href="#">
                        <img src={logo} alt="logo" className="logo_image" />
                        <span className="logo_text">함께마당</span>
                    </a>
                </h1>
                <div className="depth_area"> 
                    <nav id="depth">
                        <ul className="depth_list clearfix">
                            <li className="depth1_item">
                                <a href="#">센터소개</a>
                                <ul className="depth2_list">
                                    <li className="depth2_item">
                                        <a href="#">인사말</a>
                                    </li>
                                    <li className="depth2_item">
                                        <a href="#">시설 소개</a>
                                    </li>
                                    <li className="depth2_item">
                                        <a href="#">오시는 길</a>
                                    </li>
                                </ul>
                            </li>
                            <li className="depth1_item">
                                <a href="#">센터소개</a>
                                <ul className="depth2_list">
                                    <li className="depth2_item">
                                        <a href="#">인사말</a>
                                    </li>
                                    <li className="depth2_item">
                                        <a href="#">시설 소개</a>
                                    </li>
                                    <li className="depth2_item">
                                        <a href="#">오시는 길</a>
                                    </li>
                                </ul>
                            </li>
                            <li className="depth1_item">
                                <a href="#">센터소개</a>
                                <ul className="depth2_list">
                                    <li className="depth2_item">
                                        <a href="#">인사말</a>
                                    </li>
                                    <li className="depth2_item">
                                        <a href="#">시설 소개</a>
                                    </li>
                                    <li className="depth2_item">
                                        <a href="#">오시는 길</a>
                                    </li>
                                </ul>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
            <div className="search_wrap">
                {/* <!-- 오늘 날짜 & 온도/날씨--> */}
                <div className="info_box">
                    {/* <!-- 날짜 --> */}
                    <div className="today_box">
                        <span className="today">2025년 7월 9일</span>
                    </div>
                    {/* <!-- 온도 --> */}
                    <div className="weather_box">
                        {/* <!-- 링크 클릭시 기상청으로 이동(오늘기준) --> */}
                        <a href="#" className="weather_info" target="_blank" title="새창">
                            <span className="icon"></span>
                            <span className="temperature"><span className="ondo">33</span>℃</span>
                            <span className="weather">맑음</span>
                        </a>
                    </div>
                </div>
                {/* <!-- 검색박스 --> */}
                <div className="search_box">
                    <form className="search_form" method="post">
                        <fieldset>
                            <legend className="skip"></legend>
                            <div className="search_item">
                                <div className="search_input">
                                    {/* <!-- autocomplete - 이전에 검색했던 검색어 자동완성 속성 -->
                                    <!-- onkeypress - 사용자가 키를 눌렀을때 --> */}
                                    <label for="query" className="skip">검색어 입력</label>
                                    <input type="text" name="query" id="query" className="sch_input" placeholder="검색어를 입력해주세요." autocomplete="off" />
                                </div>
                                <div className="sch_btn_box">
                                    {/* <!-- 버튼 클릭시 통합검색 페이지 이동 --> */}
                                    <a href="#" id="btn_search">검색 버튼 클릭</a>
                                </div>
                            </div>
                        </fieldset>
                    </form>
                </div>
            </div>
        </header>

      <div id="container" className="main">
        <section id="section1" className="section"></section>
        <section id="section2" className="section"></section>
        <section id="section3" className="section"></section>
      </div>
    </div>
  );
}

export default MainLayout;
