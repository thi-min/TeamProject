import React, { useEffect, useState } from "react";
import api from "../../common/api/axios";
import { useNavigate } from "react-router-dom";
import "./Gallery.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faChevronRight } from "@fortawesome/free-solid-svg-icons";

export default function ImgBoard() {
  const [posts, setPosts] = useState([]);
  const [repImages, setRepImages] = useState({});
  const [searchKeyword, setSearchKeyword] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchType, setSearchType] = useState("all");

  const navigate = useNavigate();
  const baseUrl = "http://127.0.0.1:8090/bbs/bbslist";
  const backendUrl = "http://127.0.0.1:8090";

  // 게시글 + 대표 이미지 조회
  const fetchPosts = async (page = 0, keyword = "") => {
    try {
      const params = { type: "POTO", page, size: 12 };

      // 검색 키워드와 타입에 따라 파라미터 설정
      if (searchType !== "all" && keyword.trim() !== "") {
        params.searchType = searchType;
        if (searchType === "title") params.bbstitle = keyword.trim();
        if (searchType === "content") params.bbscontent = keyword.trim();
        // 작성자 검색 관련 조건 삭제
      }

      const res = await api.get(baseUrl, { params });

      const pageData = res.data.bbsList;
      setPosts(pageData.content || []);
      setTotalPages(pageData.totalPages || 0);
      setCurrentPage(pageData.number || 0);

      // 대표 이미지 Map 처리
      const repMap = {};
      const repImagesFromBack = res.data.representativeImages || {};
      for (const [key, value] of Object.entries(repImagesFromBack)) {
        if (value && value.imagePath) {
          repMap[key] = { ...value, imagePath: `${backendUrl}${value.imagePath}` };
        } else {
          repMap[key] = null;
        }
      }
      setRepImages(repMap);
    } catch (error) {
      console.error("데이터 불러오기 오류:", error);
      alert("게시글 조회 실패");
    }
  };

  useEffect(() => {
    fetchPosts(currentPage, searchKeyword);
  }, [currentPage]);

  const handleSearch = () => {
    setCurrentPage(0);
    fetchPosts(0, searchKeyword);
  };

  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) setCurrentPage(page);
  };

  return (
    <div className="img-board-container">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon bbs"></div>
          <div className="form_title">입양 후기 게시판</div>
        </div>
      </div>
      <div className="search_bar_box">
        <div className="temp_form_box md">
        <select className="temp_select" value={searchType} onChange={(e) => setSearchType(e.target.value)}>
          <option value="all">전체</option>
          <option value="title">제목</option>
          <option value="content">내용</option>
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
        <button className="btn" onClick={handleSearch}>조회</button>
        </div>
      </div>

      {posts.length > 0 ? (
        <div className="img-board-grid">
          {posts.map((post) => {
            const repImage = repImages[post.bulletinNum.toString()];
            return (
              <div
                className="img-board-item"
                key={post.bulletinNum}
                onClick={() => navigate(`/bbs/image/${post.bulletinNum}`)}
              >
                <div className="img-thumb">
                  {repImage && repImage.imagePath ? (
                    <img src={repImage.imagePath} alt={post.bbstitle} />
                  ) : (
                    <div className="no-image">🖼️</div>
                  )}
                </div>
                <div className="img-info">
                  <div className="title">{post.bbstitle}</div>
                  <div className="meta">
                    <span>{post.regdate?.substring(0, 10)}</span>
                    <span>조회 {post.readcount}</span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <div className="no-posts">등록된 게시물이 없습니다.</div>
      )}

      <div className="pagination">
        <button disabled={currentPage === 0} onClick={() => handlePageChange(currentPage - 1)}>
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
      <div className="form_center_box solo">
        <div className="temp_btn md">
          <button className="btn" onClick={() => navigate("/bbs/image/write")}>
            글쓰기
          </button>
        </div>
      </div>
    </div>
  );
}