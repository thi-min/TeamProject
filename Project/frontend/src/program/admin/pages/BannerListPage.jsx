import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../style/BannerManage.css";
import BannerService from "../services/BannerService";

const BannerListPage = () => {
  const [banners, setBanners] = useState([]);
  const navigate = useNavigate();

  // 배너 목록 조회
  const fetchBanners = async () => {
    try {
      const { data } = await BannerService.getAll();
      setBanners(data);
    } catch (err) {
      console.error("배너 목록 조회 실패", err);
    }
  };

  // 페이지 로드시 전체 조회
  useEffect(() => {
    fetchBanners();
  }, []);

  return (
    <div className="admin-banner-page">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon banner"></div>
          <div className="form_title">배너 관리</div>
        </div>
      </div>
      <h3>배너 목록</h3>

      {/* 결과 테이블 */}
      <table className="table type2 responsive border">
        <thead>
          <tr>
            <th>배너ID</th>
            <th>배너 제목</th>
            <th>노출 기간</th>
            <th>상태</th>

          </tr>
        </thead>
        <tbody className="text_center">
          {banners.map((b) => (
                <tr
                key={b.bannerId}
                onClick={() => navigate(`/admin/banner/${b.bannerId}`)}
                style={{ cursor: "pointer" }}
                className={!b.visible ? "row-disabled" : ""}
                >
                <td>{b.bannerId}</td>
                <td>{b.title}</td>
                <td>{b.startDate} ~ {b.endDate}</td>
                <td>{b.visible ? "활성" : "비활성"}</td>
                </tr>
            ))}
            </tbody>
      </table>

      {/* 등록 버튼 */}
      <div className="banner-button">
        <div className ="form_center_box">
            <div className="temp_btn md">
                <button className="btn" onClick={() => navigate("/admin/banner/create")}>
                등록
                </button>
            </div>
        </div>
      </div>
    </div>
  );
};

export default BannerListPage;