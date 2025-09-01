import React, { useRef, useState } from "react";
import { Link } from "react-router-dom";
//swiper
import { Swiper, SwiperSlide } from "swiper/react";
import {
  Navigation,
  Pagination,
  Autoplay,
  A11y,
  EffectFade,
} from "swiper/modules";
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";
import "swiper/css/effect-fade";

//gsap 스크롤
import gsap from "gsap";
import { useGSAP } from "@gsap/react";

//메인 맵지도
import RoughMap from "./RoughMap";

import "../style/main.css";
import visualImg01 from "../images/visual_img01.jpg";
import visualImg02 from "../images/visual_img02.jpg";

//이미지 import
import animal1 from "../images/animal1.jpeg";
import animal2 from "../images/animal2.jpeg";
import animal3 from "../images/animal3.jpeg";
import animal4 from "../images/animal4.jpeg";
import animal5 from "../images/animal5.jpeg";
import animal6 from "../images/animal6.jpeg";

export default function MainPage() {
  // Swiper 인스턴스 레퍼런스
  const swiperRef = useRef(null);

  // 재생/정지 상태
  const [isPlaying, setIsPlaying] = useState(true);

  // 외부 버튼 핸들러
  const handlePrev = () => swiperRef.current?.slidePrev();
  const handleNext = () => swiperRef.current?.slideNext();
  const handleTogglePlay = () => {
    if (!swiperRef.current) return;
    if (isPlaying) {
      // 재생 중이면 정지
      swiperRef.current.autoplay?.stop();
      setIsPlaying(false);
    } else {
      // 정지 상태면 재생
      swiperRef.current.autoplay?.start();
      setIsPlaying(true);
    }
  };

  // 슬라이드 데이터 (필요 시 확장 가능)
  const slides = [
    { src: visualImg01, alt: "비주얼 이미지 1" },
    { src: visualImg02, alt: "비주얼 이미지 2" },
  ];

  const slideMarkup = (
    <Swiper
      modules={[Navigation, Pagination, Autoplay, A11y, EffectFade]}
      effect="fade" // ✅ 페이드 전환
      fadeEffect={{ crossFade: true }} // 부드럽게 겹치며 페이드
      loop
      speed={700}
      autoplay={{
        delay: 3000,
        disableOnInteraction: false,
      }}
      pagination={{ clickable: true }}
      onBeforeInit={(swiper) => {
        swiperRef.current = swiper;
      }}
      className="main_swiper"
    >
      {slides.map((s, idx) => (
        <SwiperSlide key={idx}>
          <div className="visual_img_box">
            <img src={s.src} alt={s.alt} />
          </div>
        </SwiperSlide>
      ))}
    </Swiper>
  );

  // 재생/정지 상태
  const [isPlaying2, setIsPlaying2] = useState(true);

  // 외부 버튼 핸들러
  const handlePrev2 = () => swiperRef.current?.slidePrev();
  const handleNext2 = () => swiperRef.current?.slideNext();
  const handleTogglePlay2 = () => {
    if (!swiperRef.current) return;
    if (isPlaying2) {
      // 재생 중이면 정지
      swiperRef.current.autoplay?.stop();
      setIsPlaying2(false);
    } else {
      // 정지 상태면 재생
      swiperRef.current.autoplay?.start();
      setIsPlaying2(true);
    }
  };

  const animalimages = [
    { src: animal1, alt: "동물 이미지1" },
    { src: animal2, alt: "동물 이미지2" },
    { src: animal3, alt: "동물 이미지3" },
    { src: animal4, alt: "동물 이미지4" },
    { src: animal5, alt: "동물 이미지5" },
    { src: animal6, alt: "동물 이미지6" },
  ];

  const animalSlideList = (
    <Swiper
      modules={[Navigation, Autoplay, A11y]}
      loop
      speed={400}
      // autoplay={{
      //   delay: 3000,
      //   disableOnInteraction: false,
      // }}
      onBeforeInit={(swiper) => {
        swiperRef.current = swiper;
      }}
      slidesPerView={5}
      spaceBetween={40}
      className="main_swiper"
    >
      {animalimages.map((s, idx) => (
        <SwiperSlide key={idx}>
          <div className="animal_item">
            <Link>
              <img src={s.src} alt={s.alt} />
            </Link>
          </div>
        </SwiperSlide>
      ))}
    </Swiper>
  );
  return (
    <div className="main_page">
      <div className="rowgroup1">
        <section>
          <h2>메인 비주얼 영역</h2>
          <div className="main_visual_wrap waypoint">
            <div className="main_visual_list">
              <div className="main_visual_item">
                {/* <div className="svg_box">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    viewBox="0 0 1693 970"
                  >
                    <path
                      className="line_1"
                      fillRule="evenodd"
                      clipRule="evenodd"
                      fill="none"
                      stroke="#333"
                      strokeLinejoin="round"
                      strokeLinecap="round"
                      strokeWidth="7"
                      d="M 1162.92 969.16 c -70.78 -66.11 -179.7 -145.93 -325.07 -168.95 c -89.75 -14.22 -107.3 6.85 -250.22 0 c -124.46 -5.97 -259.44 -12.44 -391.37 -90.89 c -61.02 -36.28 -147.61 -87.77 -181.78 -191.41 c -40.14 -121.72 17.97 -233.67 36.36 -265.19 c 104.79 -179.64 342.8 -229.47 373.19 -233.11 c 142.22 -26.73 331.51 -20.25 443.76 -19.25 c 211.62 1.89 310.95 18.71 362.49 28.87 c 151.16 29.81 254.78 51.69 343.25 140.08 c 26.08 26.06 109.31 109.22 113.35 235.25 c 3.09 96.44 -41.83 167.06 -59.88 194.61 c -56.02 85.5 -131.84 122.63 -187.13 149.7 c -104.64 51.24 -204.05 61.83 -271.6 62.023"
                    />
                  </svg>
                </div> */}

                <div className="slide_item_wrap">{slideMarkup}</div>
                <div className="item_text_box">
                  <span className="text_item item1">
                    <span className="text">반려견과 함께하는</span>
                  </span>
                  <span className="text_item item2">
                    <em className="text">함께마당 입니다.</em>
                  </span>
                </div>
              </div>
            </div>
            <div className="control_box">
              <button
                type="button"
                className="slide_btn prev"
                onClick={handlePrev}
              >
                이전
              </button>
              <button
                type="button"
                className={`all ${isPlaying ? "pause" : "play"}`}
                onClick={handleTogglePlay}
              >
                {isPlaying ? "정지" : "재생"}
              </button>
              <button
                type="button"
                className="slide_btn next"
                onClick={handleNext}
              >
                다음
              </button>
            </div>
          </div>
        </section>
      </div>
      <div className="rowgroup2 row_index">
        <section>
          <div className="row_wrap">
            <div className="quick_menu_wrap waypoint">
              <div className="quick_menu_title">
                <span>Quick Menu</span>
              </div>
              <div className="quick_menu_list">
                <div className="quick_menu_item quick1">
                  <Link to="" className="quick_item_link">
                    <div className="item_box">
                      <i className="menu_icon"></i>
                      <div className="item_text">
                        <span className="title">놀이터 예약</span>
                        <span className="desc">
                          반려견과 함께 <br />
                          놀아볼까요?
                        </span>
                      </div>
                    </div>
                  </Link>
                </div>
                <div className="quick_menu_item quick2">
                  <Link to="" className="quick_item_link">
                    <div className="item_box">
                      <i className="menu_icon"></i>
                      <div className="item_text">
                        <span className="title">봉사 예약</span>
                        <span className="desc">
                          유기견들과 함께하는 <br />
                          봉사활동
                        </span>
                      </div>
                    </div>
                  </Link>
                </div>
                <div className="quick_menu_item quick3">
                  <Link to="" className="quick_item_link">
                    <div className="item_box">
                      <i className="menu_icon"></i>
                      <div className="item_text">
                        <span className="title">센터 아이들</span>
                        <span className="desc">
                          보호중인 귀여운 <br />
                          아이들 보고가세요~
                        </span>
                      </div>
                    </div>
                  </Link>
                </div>
                <div className="quick_menu_item quick4">
                  <Link to="" className="quick_item_link">
                    <div className="item_box">
                      <i className="menu_icon"></i>
                      <div className="item_text">
                        <span className="title">후원하기</span>
                        <span className="desc">
                          당신의 작은 관심이 <br />
                          아이들에게 큰 도움이 됩니다.
                        </span>
                      </div>
                    </div>
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
      <div className="rowgroup3 row_index">
        <section>
          <div className="row_wrap">
            <div className="item_area">
              <div className="bbs_box">
                <div className="bbs_title_box">
                  <span>공지사항</span>
                  <Link to="" className="bbs_move_btn">
                    더보기
                  </Link>
                </div>
                <div className="bbs_text_list">
                  <div className="bbs_text_item">
                    <Link to="">
                      <span className="link_title">
                        동해물과백두산이 마르고 닳도록 하느님이 보우하사
                        우리나라만세상에 소리쳐 너에게 닿기를
                      </span>
                      <span className="link_day">2025-06-25</span>
                    </Link>
                  </div>
                  <div className="bbs_text_item">
                    <Link to="">
                      <span className="link_title">
                        동해물과백두산이 마르고 닳도록 하느님이 보우하사
                      </span>
                      <span className="link_day">2025-06-25</span>
                    </Link>
                  </div>
                  <div className="bbs_text_item">
                    <Link to="">
                      <span className="link_title">
                        하느님이 보우하사 우리나라만세
                      </span>
                      <span className="link_day">2025-06-25</span>
                    </Link>
                  </div>
                  <div className="bbs_text_item">
                    <Link to="">
                      <span className="link_title">
                        하느님이 보우하사 우리나라만세
                      </span>
                      <span className="link_day">2025-06-25</span>
                    </Link>
                  </div>
                  <div className="bbs_text_item">
                    <Link to="">
                      <span className="link_title">
                        동해물과백두산이 터진다면 어떤가요
                      </span>
                      <span className="link_day">2025-06-25</span>
                    </Link>
                  </div>
                </div>
              </div>
              <div className="map_wrap">
                <div className="bbs_title_box">
                  <span>오시는길</span>
                  <a className="bbs_move_btn" href="/" data-discover="true">
                    더보기
                  </a>
                </div>
                <RoughMap />
              </div>
            </div>
          </div>
          <div className="img_bbs_box">
            <div className="item_area animal_bbs_box">
              <div className="animal_title">아이들 사진</div>
              <div className="animal_inner">
                <div className="animal_list">{animalSlideList}</div>
                <div className="animal_btn_box">
                  <button
                    type="button"
                    className="ani_btn prev"
                    onClick={handlePrev2}
                  >
                    이전
                  </button>
                  <button
                    type="button"
                    className="ani_btn next"
                    onClick={handleNext2}
                  >
                    다음
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
