// Project/frontend/src/admin/AdminImgDetail.jsx
// 목적: 관리자 이미지 게시판 상세 보기
// 변경사항:
//  - 이미지 src는 반드시 /DATA 또는 http(s) 경로만 사용 (download URL 배제)
//  - 관리자 파일 API가 /DATA를 안 내려주면, 사용자 상세 API(/bbs/{id})로 보조 조회하여 files 교체
//  - 작성자 표시는 언마스킹(풀네임) 우선 로직 유지

import React, { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../../common/api/axios";

import { Swiper, SwiperSlide } from "swiper/react";
import { Navigation, Pagination, Autoplay } from "swiper/modules";
import "swiper/css";
import "swiper/css/navigation";
import "swiper/css/pagination";

// ================= 상수 =================
const backendUrl = "http://127.0.0.1:8090";
const ADMIN_BASE = "http://127.0.0.1:8090/admin/bbs/poto";

// ================= 유틸 =================

// /DATA 또는 http(s) 경로인가?
const isDataLike = (s) =>
  typeof s === "string" && (s.startsWith("/DATA") || s.startsWith("http"));

// download URL 패턴인가? (관리자/사용자 모두 커버)
const isDownloadUrl = (s) =>
  typeof s === "string" &&
  (/\/admin\/bbs\/files\/\d+\/download/.test(s) ||
    /\/bbs\/files\/\d+\/download/.test(s));

// 이미지 src로 쓸 "원본 경로" 고르기: /DATA or http만 허용, download URL은 배제
const pickImageRaw = (file) => {
  const candidates = [
    file?.thumbnailPath,
    file?.imagePath,
    file?.fileUrl,
    file?.url,
    file?.path,
    file?.savePath,
  ];
  for (const c of candidates) {
    if (isDataLike(c) && !isDownloadUrl(c)) return c;
  }
  return null; // 적절한 경로가 없으면 null
};

const resolveSrc = (raw) => {
  if (!raw) return null;
  const s = String(raw);
  if (s.startsWith("/DATA") || s.startsWith("http")) return s; // 👈 그대로 사용
  return `${backendUrl}${s}`;
};

// 날짜 포맷
const toLocalDateStringFlexible = (v) => {
  if (!v) return "";
  const raw = typeof v === "string" ? v.replace(" ", "T") : v;
  const d = new Date(raw);
  return isNaN(d) ? "" : d.toLocaleDateString("ko-KR");
};

export default function AdminImgDetail() {
  const { id } = useParams(); // /admin/bbs/image/Detail/:id
  const navigate = useNavigate();

  const [post, setPost] = useState(null);
  const [files, setFiles] = useState([]);
  const [repImage, setRepImage] = useState(null);

  // 작성자(언마스킹) 표출용 상태
  const [writer, setWriter] = useState("-");

  // 슬라이더 컨트롤
  const swiperRef = useRef(null);
  const [isPlaying, setIsPlaying] = useState(true);

  // =============== 상세 조회 ===============
  const fetchPost = async () => {
    try {
      // 1) 관리자 상세
      const res = await api.get(`${ADMIN_BASE}/${id}`);
      const bbs = res.data?.bbs || res.data;
      setPost(bbs);
      setRepImage(res.data?.representativeImage || null);

      // 2) 관리자 첨부파일
      const fileRes = await api.get(`/admin/bbs/${id}/files`);
      let adminFiles = fileRes.data || [];

      // 3) 작성자(언마스킹 우선: memberName → post.member.memberName → writerName)
      const adminWriter =
        bbs?.memberName ?? bbs?.member?.memberName ?? bbs?.writerName ?? "-";
      setWriter(adminWriter);

      // 4) 파일 경로 정합성 체크
      //    - adminFiles가 /DATA(http) 경로를 하나라도 포함하면 그대로 사용
      //    - 모두 download URL만 있거나 경로가 없으면 사용자 상세에서 files를 보조로 로드
      const hasUsableDataPath = adminFiles.some((f) => !!pickImageRaw(f));

      if (!hasUsableDataPath) {
        // ⭐ 사용자 상세 API에서 /DATA가 들어있는 files를 가져와 대체
        try {
          const userRes = await api.get(`${backendUrl}/bbs/${id}`);
          const u = userRes.data?.bbs || userRes.data;
          const userFiles = userRes.data?.files || u?.files || [];
          // 사용자 응답의 작성자에 언마스킹 값이 있으면 갱신
          const userWriter =
            u?.memberName ?? u?.member?.memberName ?? u?.writerName ?? null;
          if (userWriter) setWriter(userWriter);

          setFiles(userFiles);
        } catch (e) {
          // 사용자 상세가 실패하면 관리자 파일 그대로 사용 (다운로드 링크로만 표출)
          setFiles(adminFiles);
        }
      } else {
        // 관리자 파일에 /DATA가 있으면 그대로 사용
        setFiles(adminFiles);
      }
    } catch (err) {
      console.error("상세 조회 실패:", err);
      alert("게시글 상세 조회 실패");
    }
  };

  useEffect(() => {
    fetchPost();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  // =============== 삭제 ===============
  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await api.delete(`/admin/bbs/${id}`, { params: { adminId: 1 } });
      alert("삭제 완료");
      navigate("/admin/bbs/image");
    } catch (err) {
      console.error("삭제 실패:", err);
      alert("삭제 실패");
    }
  };

  // =============== 슬라이더 외부 컨트롤 ===============
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

  if (!post) return <div>로딩 중...</div>;

  // =============== 슬라이드 마크업 (이미지는 /DATA 경로만 사용) ===============
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
            swiperRef.current = swiper;
          }}
        >
          {files.map((f) => {
            const fileNum = f.fileNum ?? f.filenum ?? f.id;
            const originalName =
              f.originalName ?? f.originalname ?? f.name ?? "첨부파일";
            const ext = (f.extension ?? f.ext ?? "").toLowerCase();
            const isImage = /(jpe?g|png|gif|webp)$/i.test(ext);

            // ✅ 반드시 /DATA(or http) 경로만 이미지로 사용
            const rawImg = pickImageRaw(f);
            const imgUrl = rawImg ? resolveSrc(rawImg) : null;

            return (
              <SwiperSlide key={fileNum ?? originalName}>
                <div className="slide_item">
                  {isImage && imgUrl ? (
                    <img src={imgUrl} alt={originalName} />
                  ) : (
                    // 이미지 경로가 없으면 다운로드 링크로만 표출
                    <a
                      href={`${backendUrl}/admin/bbs/files/${fileNum}/download`}
                      download={originalName}
                    >
                      {originalName}
                    </a>
                  )}
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

  return (
    <div className="form_item type2 bbs_form">
      <div className="img_dtl_wrap">
        {slideMarkup}

        <div className="text_box">
          <div className="text_item">
            <span className="t1">제목</span>
            <span className="t2">
              {post.bbsTitle ?? post.bbstitle ?? post.title ?? "(제목 없음)"}
            </span>
          </div>

          <div className="text_item">
            <span className="t1">작성자</span>
            <span className="t2">{writer}</span>
          </div>

          <div className="text_item">
            <span className="t1">내용</span>
            <span
              className="t2"
              dangerouslySetInnerHTML={{
                __html:
                  post.bbsContent ?? post.bbscontent ?? post.content ?? "",
              }}
            />
          </div>

          <div className="text_item">
            <span className="t1">작성일</span>
            <span className="t2">
              {toLocalDateStringFlexible(
                post.registDate ??
                  post.registdate ??
                  post.createdAt ??
                  post.created_at
              )}
            </span>
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
          <button className="btn" onClick={() => navigate("/admin/bbs/image")}>
            목록보기
          </button>
        </div>
        <div className="right_btn_box">
          <div className="temp_btn white md">
            <button
              className="btn"
              onClick={() => navigate(`/admin/bbs/image/edit/${id}`)}
            >
              수정
            </button>
          </div>
          <div className="temp_btn md">
            <button className="btn" onClick={handleDelete}>
              삭제
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
