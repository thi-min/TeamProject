import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../style/BannerManage.css";
import BannerService from "../services/BannerService";

const BannerCreatePage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    subTitle: "",
    startDate: "",
    endDate: "",
    linkUrl: "",
    visible: true,
    altText: "",
  });
  const [file, setFile] = useState(null);

  // 입력값 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // 파일 선택 핸들러
  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  // 등록 요청
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.title) {
      alert("제목은 필수입니다.");
      return;
    }
    // if (!formData.startDate || !formData.endDate) {
    //   alert("노출 기간을 입력해주세요.");
    //   return;
    // }
    if (!file) {
      alert("이미지 파일은 필수입니다.");
      return;
    }

    try {
      const requestData = new FormData();
      requestData.append(
        "data",
        new Blob([JSON.stringify(formData)], { type: "application/json" })
      );
      requestData.append("file", file);

      await BannerService.create(requestData);
      alert("배너가 등록되었습니다!");
      navigate("/admin/banner");
    } catch (err) {
      console.error("배너 등록 실패:", err);
      alert("배너 등록 실패");
    }
  };

  return (
    <div className="admin-banner-page">
      <div className="form_top_box">
        <div className="form_top_item">
          <div className="form_icon banner"></div>
          <div className="form_title">배너 등록</div>
        </div>
      </div>
      <h3>배너 등록</h3>

      <div className="required-info">
        <span className="required">*</span> 표시는 필수 입력항목입니다.
      </div>
      <form onSubmit={handleSubmit} className="banner-form">
        <table className="table type2 responsive border">
          <colgroup>
            <col className="w20p" />
            <col />
          </colgroup>
          <tbody>
            <tr>
              <th>
                배너 제목 <span className="required">*</span>
              </th>
              <td>
                <div class="temp_form md w30p">
                <input
                  type="text"
                  name="title"
                  class="temp_input"
                  value={formData.title}
                  onChange={handleChange}
                  required
                />
                </div>
              </td>
            </tr>

            <tr>
              <th>배너 부제목</th>
              <td>
                <div class="temp_form md w30p">
                <input
                  type="text"
                  name="subTitle"
                  class="temp_input"
                  value={formData.subTitle}
                  onChange={handleChange}
                />
                </div>
              </td>
            </tr>

            <tr>
              <th>
                노출 기간 <span className="required">*</span>
              </th>
              <td className="all_day">
                <div class="temp_form md w20p">
                <input
                  type="date"
                  name="startDate"
                  className="temp_input"
                  value={formData.startDate}
                  onChange={handleChange}
                />
                </div>
                <span> ~ </span>
                <div class="temp_form md w20p">
                <input
                  type="date"
                  name="endDate"
                  className="temp_input"
                  value={formData.endDate}
                  onChange={handleChange}
                />
                </div>
              </td>
            </tr>

            <tr>
              <th>
                이미지 업로드 <span className="required">*</span>
              </th>
              <td>
                <div class="temp_form md w30p">
                <input
                  type="file"
                  accept="image/*"
                  class="temp_input"
                  onChange={handleFileChange}
                  required
                />
                </div>
              </td>
            </tr>

            <tr>
              <th>이미지 설명(alt)</th>
              <td>
                <div class="temp_form md w30p">
                <input
                  type="text"
                  name="altText"
                  class="temp_input"
                  value={formData.altText}
                  onChange={handleChange}
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
                  name="linkUrl"
                  class="temp_input"
                  value={formData.linkUrl}
                  onChange={handleChange}
                />
                </div>
              </td>
            </tr>

            <tr>
              <th>
                노출 여부 <span className="required">*</span>
              </th>
              <td>
                <div class="temp_form_box md">
                <select
                  name="visible"
                  class="temp_select"
                  value={formData.visible}
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

        <div className="form_center_box">
          <div className="temp_btn white md">
            <button type="button" className="btn" onClick={() => navigate(-1)}>
              목록보기
            </button>
          </div>
          <div className="temp_btn md">
            <button type="submit" className="btn">
              등록
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};

export default BannerCreatePage;
