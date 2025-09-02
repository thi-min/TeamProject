import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import BannerService from "../services/BannerService";
import "../style/BannerManage.css";

const AdminBannerDetailPage = () => {
  const { bannerId } = useParams();
  const navigate = useNavigate();
  const [banner, setBanner] = useState(null);
  const [formData, setFormData] = useState({});
  const [file, setFile] = useState(null);
  const [isEditing, setIsEditing] = useState(false);

  // 상세 조회
  const fetchBannerDetail = async () => {
    try {
      const { data } = await BannerService.getById(bannerId);
      setBanner(data);
    } catch (err) {
      console.error("배너 상세 조회 실패", err);
      alert("상세 정보를 불러오지 못했습니다.");
    }
  };

  useEffect(() => {
    fetchBannerDetail();
  }, [bannerId]);

  // 수정 모드 진입
  const handleEdit = () => {
    setFormData(banner);
    setIsEditing(true);
  };

  // 수정 저장
  const handleUpdate = async () => {
    try {
      const requestData = new FormData();
      requestData.append(
        "data",
        new Blob([JSON.stringify(formData)], { type: "application/json" })
      );
      if (file) requestData.append("file", file);

      await BannerService.update(banner.bannerId, requestData);
      alert("수정 완료");
      setIsEditing(false);
      fetchBannerDetail(); // 최신 데이터 다시 로드
    } catch (err) {
      console.error("수정 실패", err);
      alert("수정 실패");
    }
  };

  // 삭제
  const handleDelete = async () => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await BannerService.delete(bannerId);
      alert("삭제되었습니다.");
      navigate("/admin/banner");
    } catch (err) {
      console.error("삭제 실패", err);
      alert("삭제 실패");
    }
  };

  if (!banner) return <p>로딩 중...</p>;

  return (
    <div className="admin-banner-detail">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon banner"></div>
          <div className="form_title">배너 상세</div>
        </div>
      </div>
      <h3>배너 상세 정보</h3>

      {!isEditing ? (
        // ---------------------- 상세보기 모드 ----------------------
        <div className="form_wrap">
          <table className="table type2 responsive border">
            <tbody>
              <tr>
                <th>ID</th>
                <td>{banner.bannerId}</td>
              </tr>
              <tr>
                <th>제목</th>
                <td>{banner.title}</td>
              </tr>
              <tr>
                <th>부제목</th>
                <td>{banner.subTitle || "-"}</td>
              </tr>
              <tr>
                <th>노출 기간</th>
                <td>
                  {banner.startDate} ~ {banner.endDate}
                </td>
              </tr>
              <tr>
                <th>이미지</th>
                <td>
                  <img
                    src={`/DATA/banner/${banner.imageUrl}`}
                    alt={banner.altText || "배너 이미지"}
                    style={{ maxWidth: "300px", borderRadius: "8px" }}
                  />
                </td>
              </tr>
              <tr>
                <th>이미지 설명</th>
                <td>{banner.altText || "-"}</td>
              </tr>
              <tr>
                <th>링크 URL</th>
                <td>{banner.linkUrl || "-"}</td>
              </tr>
              <tr>
                <th>노출 여부</th>
                <td>{banner.visible ? "활성" : "비활성"}</td>
              </tr>
            </tbody>
          </table>
        </div>
      ) : (
        // ---------------------- 수정 모드 ----------------------
        <div className="form_wrap">
          <form className="banner-edit-form">
            <table className="table type2 responsive border">
              <tbody>
                <tr>
                  <th>제목</th>
                  <td>
                    <div class="temp_form md w30p">
                      <input
                        type="text"
                        class="temp_input"
                        value={formData.title || ""}
                        onChange={(e) =>
                          setFormData({ ...formData, title: e.target.value })
                        }
                      />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th>부제목</th>
                  <td>
                    <div class="temp_form md w30p">
                      <input
                        type="text"
                        class="temp_input"
                        value={formData.subTitle || ""}
                        onChange={(e) =>
                          setFormData({ ...formData, subTitle: e.target.value })
                        }
                      />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th>노출 기간</th>
                  <td className="all_day">
                    <div class="temp_form md w20p">
                    <input
                      type="date"
                      class="temp_input"
                      value={formData.startDate || ""}
                      onChange={(e) =>
                        setFormData({ ...formData, startDate: e.target.value })
                      }
                    />
                    </div>
                    <span> ~ </span>
                    <div class="temp_form md w20p">
                    <input
                      type="date"
                      class="temp_input"
                      value={formData.endDate || ""}
                      onChange={(e) =>
                        setFormData({ ...formData, endDate: e.target.value })
                      }
                    />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th>이미지</th>
                  <td>
                    <div class="temp_form md w30p">
                    <input
                      type="file"
                      class="temp_input"
                      onChange={(e) => setFile(e.target.files[0])}
                    />
                    </div>
                    <p style={{ fontSize: "12px", color: "#666" }}>
                      새 이미지를 선택하지 않으면 기존 이미지가 유지됩니다.
                    </p>
                  </td>
                </tr>
                <tr>
                  <th>이미지 설명</th>
                  <td>
                    <div class="temp_form md w30p">
                    <input
                      type="text"
                      class="temp_input"
                      value={formData.altText || ""}
                      onChange={(e) =>
                        setFormData({ ...formData, altText: e.target.value })
                      }
                    />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th>링크 URL</th>
                  <td>
                    <div class="temp_form md w30p">
                    <input
                      type="text"
                      class="temp_input"
                      value={formData.linkUrl || ""}
                      onChange={(e) =>
                        setFormData({ ...formData, linkUrl: e.target.value })
                      }
                    />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th>노출 여부</th>
                  <td>
                    <div class="temp_form_box md">
                      <select
                        value={formData.visible}
                        class="temp_select"
                        onChange={(e) =>
                          setFormData({
                            ...formData,
                            visible: e.target.value === "true",
                          })
                        }
                      >
                        <option value="true">활성</option>
                        <option value="false">비활성</option>
                      </select>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </form>
        </div>
      )}

      {/* 버튼 영역 */}
      <div className="form_center_box">
        <div className="temp_btn white md">
          <button type="button" className="btn" onClick={() => navigate("/admin/banner")}>
            목록보기
          </button>
        </div>

        <div className="right_btn_box">
          {!isEditing ? (
            <>
              <div className="temp_btn md">
                <button type="button" className="btn" onClick={handleEdit}>
                  수정
                </button>
              </div>
              <div className="temp_btn danger md">
                <button type="button" className="btn" onClick={handleDelete}>
                  삭제
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="temp_btn md">
                <button type="button" className="btn" onClick={handleUpdate}>
                  저장
                </button>
              </div>
              <div className="temp_btn white md">
                <button
                  type="button"
                  className="btn"
                  onClick={() => setIsEditing(false)}
                >
                  취소
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default AdminBannerDetailPage;
