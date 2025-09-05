// ImgDetail.jsx - 수정된 코드
import React, { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";
import ChatPopup from "../../program/chat/services/ChatPopup";

// Swiper import
import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Autoplay } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";

export default function ImgDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [files, setFiles] = useState([]);
  const [repImage, setRepImage] = useState(null);
  const [isChatOpen, setIsChatOpen] = useState(false); // 채팅 팝업 상태 추가
  const backendUrl = "http://127.0.0.1:8090";

  // Swiper 인스턴스 레퍼런스
  const swiperRef = useRef(null);

  // 재생/정지 상태
  const [isPlaying, setIsPlaying] = useState(true);

  // 채팅 팝업 토글 함수 추가
  const toggleChat = () => {
    setIsChatOpen(!isChatOpen);
  };

  // 게시글 상세 조회
  const fetchPost = async () => {
    try {
      const res = await api.get(`${backendUrl}/bbs/${id}`);
      const bbs = res.data.bbs || res.data;

      setPost(bbs);
      setFiles(res.data.files || []);
      setRepImage(res.data.representativeImage || null);
    } catch (error) {
      console.error("상세 조회 실패:", error);
      alert("게시글 상세 조회 실패");
    }
  };

  useEffect(() => {
    fetchPost();
  }, [id]);

  // 게시글 삭제
  const handleDelete = async () => {
    const memberNum = localStorage.getItem("memberNum");
    if (!memberNum) {
      alert("로그인이 필요합니다.");
      return;
    }
    if (!window.confirm("정말 삭제하시겠습니까?")) return;

    try {
      await api.delete(`${backendUrl}/bbs/${id}?memberNum=${memberNum}`);
      alert("게시글 삭제 성공");
      navigate("/bbs/image");
    } catch (error) {
      console.error("삭제 실패:", error);
      alert("게시글 삭제 실패");
    }
  };

  // // 게시글 수정 페이지 이동
  // const handleEdit = () => {
  //   navigate(`/bbs/image/edit/${id}`);
  // };

  // 외부 버튼 핸들러
  const handlePrev = () => swiperRef.current?.slidePrev();
  const handleNext = () => swiperRef.current?.slideNext();
  const handleTogglePlay = () => {
    if (!swiperRef.current) return;
    if (isPlaying) {
      swiperRef.current.autoplay?.stop();
      setIsPlaying(false);
    } else {
      swiperRef.current.autoplay?.start();
      setIsPlaying(true);
    }
  };

  // 입양 상담 버튼 클릭 핸들러 추가
  const handleAdoptConsult = () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      alert("로그인이 필요한 서비스입니다.");
      navigate("/member/login");
      return;
    }
    toggleChat();
  };

  // ✅ return 밖으로 뺀 슬라이드 JSX
  const slideMarkup = (
    <div className="img_slide_box">
      {files.length > 0 ? (
        <Swiper
          modules={[Navigation, Pagination, Autoplay]}
          navigation
          pagination={{ clickable: true }}
          spaceBetween={10}
          slidesPerView={1}
          loop
          autoplay={{ delay: 3000, disableOnInteraction: false }}
          onBeforeInit={(swiper) => {
            swiperRef.current = swiper; // ref 연결
          }}
        >
          {files.map((f) => {
            const imgUrl = f.fileUrl?.startsWith("http")
              ? f.fileUrl
              : `${backendUrl}${f.fileUrl}`;

            return (
              <SwiperSlide key={f.fileNum}>
                <div className="slide_item">
                  <img src={imgUrl} alt={f.originalName} />
                </div>
              </SwiperSlide>
            );
          })}
        </Swiper>
      ) : (
        <div className="no-image">첨부파일이 없습니다.</div>
      )}
    </div>
  );

  if (!post) return <div>로딩 중...</div>;

  return (
    <div className="form_item type2 bbs_form">
      <div className="img_dtl_wrap">
        {/* ✅ 여기서는 slideMarkup만 호출 */}
        {slideMarkup}
        <div className="text_box">
          <div className="text_item">
            <span className="t1">제목</span>
            <span className="t2">{post.bbsTitle}</span>
          </div>
          <div className="text_item">
            <span className="t1">내용</span>
            <span
              className="t2"
              dangerouslySetInnerHTML={{ __html: post.bbsContent }}
            />
          </div>
          <div className="text_item">
            <span className="t1">작성일</span>
            <span className="t2">
              {post.registDate ? post.registDate.substring(0, 10) : ""}
            </span>
          </div>
          <div className="chat_btn_box">
            <button
              type="button"
              className="chat_btn"
              onClick={handleAdoptConsult}
            >
              입양 상담
            </button>
          </div>
        </div>
      </div>
      <div className="img_slide_controls">
        <div className="btn_box_wrap">
          <button className="slide_btn" onClick={handlePrev}>
            이전
          </button>
          <button
            className={`all ${isPlaying ? "pause" : "play"}`}
            onClick={handleTogglePlay}
          >
            {isPlaying ? "정지" : "재생"}
          </button>
          <button className="slide_btn next" onClick={handleNext}>
            다음
          </button>
        </div>
      </div>
      <div className="form_center_box ">
        <div className="temp_btn white md">
          <button className="btn" onClick={() => navigate("/bbs/image")}>
            목록보기
          </button>
        </div>
        {/* <div className="right_btn_box">
          <div className="temp_btn white md">
            <button className="btn" onClick={handleEdit}>
              수정
            </button>
          </div>
          <div className="temp_btn md">
            <button className="btn" onClick={handleDelete}>
              삭제
            </button>
          </div>
        </div> */}
      </div>

      {/* 채팅 팝업 추가 */}
      {isChatOpen && <ChatPopup onClose={toggleChat} />}
    </div>
  );
}
