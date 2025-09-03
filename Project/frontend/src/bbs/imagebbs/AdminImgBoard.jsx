// Project/frontend/src/admin/ImgBoard.jsx
// 목적: 관리자 이미지 게시판 목록 (사용자 리스트를 기준으로 마크업/클래스/이미지 처리 통일)
// 주요 변경점:
// 1) 날짜/제목/내용/대표이미지 키 불일치 대비 폴백 처리(getFirst + toLocalDateStringFlexible)
// 2) <Link>에 onClick+navigate 대신 to 속성 사용 (정석)
// 3) React 속성(class -> className) 수정
// 4) 대표이미지 경로 우선순위(thumb -> original) 및 /DATA, http 처리 동일화
// 5) 페이지/검색 파라미터는 관리자 API 규약(page, size, total, page, list)에 맞춤

import React, { useEffect, useState } from "react";
import api from "../../common/api/axios";
import { Link, useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faChevronLeft,
  faChevronRight,
} from "@fortawesome/free-solid-svg-icons";

// ---------- 공통 유틸: 첫 번째로 값이 존재하는 키를 안전하게 선택 ----------
function getFirst(obj, keys, fallback = undefined) {
  for (const k of keys) {
    const v = obj?.[k];
    if (v !== undefined && v !== null && v !== "") return v;
  }
  return fallback;
}

// ---------- 공통 유틸: 문자열/숫자/ISO/스페이스 구분 없는 날짜 안전 파싱 ----------
function toLocalDateStringFlexible(v) {
  if (!v) return "-";
  // "yyyy-MM-dd HH:mm:ss" -> "yyyy-MM-ddTHH:mm:ss" 로 치환 (브라우저 파싱 호환)
  const raw = typeof v === "string" ? v.replace(" ", "T") : v;
  const d = new Date(raw);
  return isNaN(d) ? "-" : d.toLocaleDateString("ko-KR");
}

export default function AdminImgBoard() {
  // ---------- 상태 ----------
  const [posts, setPosts] = useState([]);
  const [repImages, setRepImages] = useState({});
  const [searchKeyword, setSearchKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");
  const [selectedPosts, setSelectedPosts] = useState([]);

  const navigate = useNavigate();

  // ✅ 관리자 API 엔드포인트 (백엔드 규약 유지)
  const baseUrl = "http://127.0.0.1:8090/admin/bbs/poto";

  // ✅ 관리자 페이지 목록 개수 (사용자와 다를 수 있음 → 관리자 기준으로 유지)
  const pageSize = 12;

  // ✅ 이미지 경로 prefix (사용자 페이지와 동일 규칙)
  const backendUrl = "http://127.0.0.1:8090";
  const resolveSrc = (raw) => {
    if (!raw) return null;
    const s = String(raw);
    // /DATA 또는 절대 URL이면 그대로 사용
    if (s.startsWith("/DATA") || s.startsWith("http")) return s;
    // 그 외는 백엔드 prefix 부착
    return `${backendUrl}${s}`;
  };

  // ---------- 게시글 + 대표 이미지 조회 ----------
  const fetchPosts = async (page = 0, keyword = "") => {
    try {
      const params = { page, size: pageSize };

      // 검색 파라미터 (백엔드 규약에 맞춤)
      if (searchType !== "all" && keyword.trim() !== "") {
        if (searchType === "title") params.bbstitle = keyword.trim();
        if (searchType === "content") params.bbscontent = keyword.trim();
        if (searchType === "writer") params.memberName = keyword.trim();
      }

      const res = await api.get(baseUrl, { params });

      // ✅ 백엔드 응답 규약 가정:
      // {
      //   list: [...],              // 게시글 목록
      //   total: 123,               // 전체 개수
      //   page: 0,                  // 현재 페이지
      //   representativeImages: { "78": { thumbnailPath, imagePath, ... }, ... }
      // }
      const postsArray = res.data.list || [];
      setPosts(postsArray);

      const totalItems = res.data.total ?? 0;
      setTotalPages(Math.ceil(totalItems / pageSize));
      setCurrentPage(res.data.page ?? 0);

      // 대표 이미지 Map 구성
      const repMap = {};
      const repImagesFromBack = res.data.representativeImages || {};

      // 키가 "문자열 bulletinNum" 으로 올 수도 있고, 숫자 키로 올 수도 있으므로 모두 지원
      for (const [key, value] of Object.entries(repImagesFromBack)) {
        if (!value) {
          repMap[key] = null;
          continue;
        }
        // 썸네일 우선 → 원본
        const raw = value.thumbnailPath || value.imagePath || null;
        const normalized = raw
          ? { ...value, imagePath: resolveSrc(raw) }
          : null;

        // 원본 키 그대로 매핑
        repMap[key] = normalized;

        // 숫자 bulletinNum 키가 필요한 경우를 대비해 중복 매핑
        const asNumber = Number(key);
        if (!Number.isNaN(asNumber)) repMap[asNumber] = normalized;
      }
      setRepImages(repMap);

      // 체크박스 초기화
      setSelectedPosts([]);
    } catch (error) {
      console.error("게시글 조회 실패:", error);
      alert("게시글 조회 실패");
      setPosts([]);
      setTotalPages(0);
      setCurrentPage(0);
      setRepImages({});
      setSelectedPosts([]);
    }
  };

  useEffect(() => {
    fetchPosts(currentPage, searchKeyword);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage]);

  // ---------- 검색 ----------
  const handleSearch = () => {
    setCurrentPage(0);
    fetchPosts(0, searchKeyword);
  };

  // ---------- 페이지 변경 ----------
  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) setCurrentPage(page);
  };

  // ---------- 전체 선택 / 해제 ----------
  const handleSelectAll = (checked) => {
    if (checked) {
      setSelectedPosts(
        posts.map((p) => getFirst(p, ["bulletinNum", "bulletinnum"]))
      );
    } else {
      setSelectedPosts([]);
    }
  };

  // ---------- 개별 체크박스 ----------
  const handleCheckboxChange = (id) => {
    setSelectedPosts((prev) =>
      prev.includes(id) ? prev.filter((pid) => pid !== id) : [...prev, id]
    );
  };

  // ---------- 선택 삭제 ----------
  const handleDeleteSelected = async () => {
    if (selectedPosts.length === 0) {
      alert("삭제할 게시글을 선택하세요.");
      return;
    }
    if (!window.confirm("선택한 게시글을 삭제하시겠습니까?")) return;

    try {
      // 관리자 delete-multiple 규약 (ids를 콤마로 전달)
      await api.delete("/admin/bbs/delete-multiple", {
        params: { adminId: 1, ids: selectedPosts.join(",") },
      });
      alert("삭제 완료");
      fetchPosts(currentPage, searchKeyword);
    } catch (err) {
      console.error(err);
      alert("삭제 실패");
    }
  };

  return (
    <div className="img_bbs_wrap type2">
      {/* 사용자 페이지와 동일한 상단 타이틀 마크업/클래스 */}
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">이미지 게시판 관리</div>
        </div>
      </div>

      {/* 검색창 (사용자와 동일 구조 + writer 옵션 추가) */}
      <div className="search_bar_box">
        <div className="temp_form_box md">
          <select
            className="temp_select"
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
          >
            <option value="all">전체</option>
            <option value="title">제목</option>
            <option value="content">내용</option>
            <option value="writer">작성자</option>
          </select>
        </div>
        <div className="temp_form md w30p">
          <input
            type="text"
            className="temp_input"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="검색어를 입력하세요"
          />
        </div>
        <div className="temp_btn md">
          <button className="btn" onClick={handleSearch}>
            조회
          </button>
        </div>
      </div>

      {/* 상단 메뉴 + 전체 선택 체크박스 (class -> className 수정) */}
      <div className="top-bar">
        <div className="temp_form md">
          <input
            id="chk-all"
            type="checkbox"
            className="temp_check"
            checked={selectedPosts.length === posts.length && posts.length > 0}
            onChange={(e) => handleSelectAll(e.target.checked)}
          />
          <label htmlFor="chk-all">전체 선택</label>
        </div>
      </div>

      {/* 게시글 목록 (사용자 페이지와 동일한 리스트 마크업/클래스) */}
      {posts.length > 0 ? (
        <div className="img_bbs_list">
          {posts.map((post) => {
            // ---------- 필드 표준화: 사용자/관리자/백엔드 불일치 대응 ----------
            const bulletinNum = getFirst(post, ["bulletinNum", "bulletinnum"]);
            const detailUrl = `/admin/bbs/image/Detail/${encodeURIComponent(
              bulletinNum
            )}`;
            const title = getFirst(
              post,
              ["bbsTitle", "bbstitle", "title"],
              "(제목 없음)"
            );
            const content = getFirst(
              post,
              ["bbsContent", "bbscontent", "content"],
              ""
            );
            const registDate = getFirst(post, [
              "registDate",
              "registdate",
              "regist_date",
              "createdAt",
              "created_at",
            ]);
            const dateText = toLocalDateStringFlexible(registDate);

            // 대표 이미지: 키가 문자열/숫자 혼재 가능 → repImages에서 모두 지원되도록 fetch 시 중복 매핑함
            const repImage =
              repImages[bulletinNum?.toString()] ?? repImages[bulletinNum];

            return (
              <div
                className={`img_bbs_item ${
                  selectedPosts.includes(bulletinNum) ? "selected" : ""
                }`}
                key={bulletinNum}
              >
                {/* 개별 체크박스 */}
                <input
                  type="checkbox"
                  className="item_checkbox"
                  checked={selectedPosts.includes(bulletinNum)}
                  onChange={() => handleCheckboxChange(bulletinNum)}
                />

                {/* 게시글 이미지 + 클릭 이동: 사용자와 동일하게 <Link to="..."> 사용 */}
                <Link
                  className="img_link"
                  to={detailUrl}
                  onClick={(e) => e.stopPropagation()}
                >
                  <div className="ima_box">
                    {repImage && repImage.imagePath ? (
                      <img src={repImage.imagePath} alt={title} />
                    ) : (
                      <div className="no-image">🖼️</div>
                    )}
                  </div>
                </Link>

                {/* 게시글 정보 (사용자와 동일 구조) */}
                <div className="img_info">
                  <div className="title">{title}</div>
                  <div className="text">
                    {content}
                    {/* 필요한 경우 조회수/작성자 등 추가 */}
                    {/* <span className="count_text">조회 {post.readcount}</span> */}
                  </div>
                  {/* ✅ 관리자 측 요청 포인트: 작성일 노출 (사용자와 동일 규칙) */}
                  <div className="date">{dateText}</div>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="no-posts">등록된 게시물이 없습니다.</div>
      )}

      {/* 페이지네이션 (사용자와 동일 구조) */}
      <div className="pagination">
        <button
          disabled={currentPage === 0}
          onClick={() => handlePageChange(currentPage - 1)}
        >
          <FontAwesomeIcon icon={faChevronLeft} />
        </button>
        {Array.from({ length: Math.max(totalPages, 1) }, (_, idx) => (
          <button
            key={idx}
            className={idx === currentPage ? "active" : ""}
            onClick={() => handlePageChange(idx)}
          >
            {idx + 1}
          </button>
        ))}
        <button
          disabled={currentPage === Math.max(totalPages, 1) - 1}
          onClick={() => handlePageChange(currentPage + 1)}
        >
          <FontAwesomeIcon icon={faChevronRight} />
        </button>
      </div>

      {/* 하단 버튼 (선택 삭제) */}
      <div className="form_center_box solo">
        <div className="temp_btn md">
          <button className="btn" onClick={handleDeleteSelected}>
            삭제
          </button>
        </div>
      </div>
    </div>
  );
}
