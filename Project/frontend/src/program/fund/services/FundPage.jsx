// import axios from 'axios';
import { api } from "../../../common/api/axios.js";
import { useState, useEffect, useRef } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import "../style/Fund.css"; // 경로 수정

// 후원 섹션 컴포넌트
const FundSection = ({ title, description, icon, onDonateClick }) => {
  return (
    <div className="fund-section-card">
      <div className="fund-section-icon">{icon}</div>
      <div className="form_title">{title}</div>
      <p className="fund-section-description">{description}</p>
      <button className="fund-donate-button" onClick={onDonateClick}>
        후원하기
      </button>
    </div>
  );
};

// 메인 후원 페이지 컴포넌트
const FundMainPage = () => {
  const navigate = useNavigate();

  const fundSections = [
    {
      title: "후원금",
      description:
        "금전적 지원을 통해 다양한 프로젝트와 활동에 도움을 줄 수 있습니다. 소중한 후원금은 투명하게 사용됩니다.",
      icon: "💸",
      path: "/funds/donation",
    },
    {
      title: "후원물품",
      description:
        "필요한 물품을 직접 후원하여 더 직접적이고 실질적인 도움을 전할 수 있습니다.",
      icon: "🎁",
      path: "/funds/goods",
    },
    {
      title: "정기후원",
      description:
        "정기적인 후원을 통해 지속가능한 지원과 안정적인 운영을 도모할 수 있습니다.",
      icon: "💖",
      path: "/funds/regular",
    },
  ];

  return (
    <div className="fund-main-page">
      <div className="fund-main-container">
        <h1 className="fund-main-title">후원 정보</h1>
        <div className="fund-section-grid">
          {fundSections.map((section, index) => (
            <FundSection
              key={index}
              title={section.title}
              description={section.description}
              icon={section.icon}
              onDonateClick={() => navigate(section.path)}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

// 후원금 신청서 컴포넌트
const FundApplicationForm = () => {
  const navigate = useNavigate();
  const [message, setMessage] = useState(null);
  const [formData, setFormData] = useState({
    applicantName: "",
    contact: { part1: "010", part2: "", part3: "" },
    birthDate: "",
    confirmationRequired: "",
    fundAmount: "",
    notes: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "contact") {
      const parts = value.split("-");
      setFormData((prev) => ({
        ...prev,
        contact: {
          part1: parts[0] || "",
          part2: parts[1] || "",
          part3: parts[2] || "",
        },
      }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 1. 로컬 스토리지에서 memberNum 가져오기
    const memberNum = localStorage.getItem("memberNum");
    const memberId = memberNum ? Number(memberNum) : null;

    if (
      !formData.applicantName ||
      !formData.contact.part2 ||
      !formData.contact.part3 ||
      !formData.birthDate ||
      !formData.confirmationRequired ||
      !formData.fundAmount
    ) {
      setMessage("모든 필수 항목을 입력해야 합니다.");
      setTimeout(() => setMessage(null), 3000);
      return;
    }

    try {
      const fundCheckStatus =
        formData.confirmationRequired === "필요" ? "Y" : "N";

      const requestData = {
        memberId: memberId, // 동적으로 설정된 memberId 사용
        fundSponsor: formData.applicantName,
        fundPhone: `${formData.contact.part1}-${formData.contact.part2}-${formData.contact.part3}`,
        fundBirth: formData.birthDate,
        fundType: "REGULAR",
        fundMoney: formData.fundAmount,
        fundNote: formData.notes,
        fundCheck: fundCheckStatus,
      };

      const response = await api.post("/funds/request", requestData);

      if (response.status === 200 || response.status === 201) {
        navigate("/funds/donation-details", {
          state: { formData: response.data },
        });
      }
    } catch (error) {
      console.error("후원금 신청 중 오류 발생:", error);
      setMessage("신청 중 오류가 발생했습니다. 다시 시도해주세요.");
      setTimeout(() => setMessage(null), 3000);
    }
  };

  return (
    <div>
      <div>
        <h3>후원금 신청서</h3>

        <form onSubmit={handleSubmit}>
          <div className="form_wrap">
            <table className="table type2 responsive border">
              <colgroup>
                <col className="w30p" />
                <col />
              </colgroup>
              <tbody>
                <tr>
                  <th scope="row">신청자명</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        type="text"
                        id="applicantName"
                        name="applicantName"
                        value={formData.applicantName}
                        onChange={handleChange}
                        className="temp_input"
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">연락처</th>
                  <td className="phone_form">
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part1}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part1: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="3" // 최대 입력 길이 제한
                      />
                    </div>
                    <span>-</span>
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part2}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part2: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="4" // 최대 입력 길이 제한
                      />
                    </div>
                    <span>-</span>
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part3}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part3: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="4" // 최대 입력 길이 제한
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">생년월일</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        className="temp_input"
                        type="date"
                        id="birthDate"
                        value={formData.birthDate}
                        name="birthDate"
                        onChange={handleChange}
                      />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row">후원확인서 필 여부</th>
                  <div className="temp_form_box">
                    <td>
                      <div className="temp_form md w40p">
                        <select
                          id="confirmationRequired"
                          name="confirmationRequired"
                          value={formData.confirmationRequired}
                          onChange={handleChange}
                          className="temp_select"
                        >
                          <option value="">선택</option>
                          <option value="필요">필요</option>
                          <option value="불필요">불필요</option>
                        </select>
                      </div>
                    </td>
                  </div>
                </tr>
                <tr>
                  <th scope="row">후원 금액</th>
                  <td className="all_day">
                    <div className="temp_form md w40p">
                      <input
                        type="text"
                        id="fundAmount"
                        name="fundAmount"
                        value={formData.fundAmount}
                        onChange={handleChange}
                        className="temp_input"
                      />
                    </div>
                    <span>원</span>
                  </td>
                </tr>

                <tr>
                  <th scope="row">비고</th>
                  <td>
                    <div className="form-input-item">
                      <textarea
                        id="notes"
                        name="notes"
                        value={formData.notes}
                        onChange={handleChange}
                        className="form-textarea w40p"
                        rows="3"
                        placeholder="비고 입력"
                      ></textarea>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div className="form_center_box">
            <div className="form_btn_box">
              <div>
                <button
                  type="button"
                  onClick={() => navigate("/funds")}
                  className="form-button-secondary"
                >
                  이전
                </button>
              </div>
            </div>
            <div className="form_btn_box">
              <div>
                <button type="submit" className="form-button-primary">
                  다음
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>

      {message && <div className="form-message">{message}</div>}
    </div>
  );
};

// 후원금 신청 내역 컴포넌트
const FundApplicationDetails = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const responseData = location.state?.formData;

  if (!responseData) {
    return (
      <div className="application-details-error">
        <p>잘못된 접근입니다. 후원금 신청서를 먼저 작성해주세요.</p>
        <button
          onClick={() => navigate("/funds/donation")}
          className="form-button-primary mt-4"
        >
          신청서로 이동
        </button>
      </div>
    );
  }

  // ⭐️ FundCheck 값에 따라 '필요' 또는 '불필요'로 변환
  const confirmationText = responseData.fundCheck === "Y" ? "필요" : "불필요";

  return (
    <div>
      <div>
        <h3>후원금 신청 내역</h3>
        <div className="form_wrap">
          <table className="table type2 responsive border">
            <colgroup>
              <col className="w30p" />
              <col />
            </colgroup>
            <tbody>
              <tr>
                <th scope="row">신청자명</th>
                <td>
                  <div className="form_desc">{responseData.fundSponsor}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">연락처</th>
                <td>
                  <div className="form_desc">{responseData.fundPhone}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">생년월일</th>
                <td>
                  <div className="form_desc">{responseData.fundBirth}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">후원확인서 필 여부</th>
                <td>
                  <div className="form_desc">{confirmationText}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">후원 금액</th>
                <td>
                  <div className="form_desc">
                    {responseData.fundMoney.toLocaleString()} 원
                  </div>
                </td>
              </tr>

              <tr>
                <th scope="row">비고</th>
                <td>
                  <div className="form_desc">
                    {responseData.fundNote || "없음"}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div className="form_center_box">
          <div className="form_btn_box">
            <div>
              <button
                onClick={() => navigate("/funds/donation")}
                className="form-button-secondary"
              >
                이전
              </button>
            </div>
          </div>
          <div className="form_btn_box">
            <div>
              <button
                onClick={() => navigate("/funds")}
                className="form-button-primary"
              >
                메인으로 이동
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// 후원 물품 신청서 컴포넌트
const GoodsApplicationForm = () => {
  const navigate = useNavigate();
  const [message, setMessage] = useState(null);
  const [formData, setFormData] = useState({
    applicantName: "",
    contact: { part1: "010", part2: "", part3: "" },
    birthDate: "",
    confirmationRequired: "",
    goods: "",
    notes: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "contact") {
      const parts = value.split("-");
      setFormData((prev) => ({
        ...prev,
        contact: {
          part1: parts[0] || "",
          part2: parts[1] || "",
          part3: parts[2] || "",
        },
      }));
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // 1. 로컬 스토리지에서 memberNum 가져오기
    const memberNum = localStorage.getItem("memberNum");
    const memberId = memberNum ? Number(memberNum) : null;

    if (
      !formData.applicantName ||
      !formData.contact.part1 ||
      !formData.contact.part2 ||
      !formData.contact.part3 ||
      !formData.birthDate ||
      !formData.confirmationRequired
    ) {
      setMessage("모든 필수 항목을 입력해야 합니다.");
      setTimeout(() => setMessage(null), 3000);
      return;
    }

    try {
      const fundCheckStatus =
        formData.confirmationRequired === "필요" ? "Y" : "N";

      const requestData = {
        memberId: memberId, // 동적으로 설정된 memberId 사용
        fundSponsor: formData.applicantName,
        fundPhone: `${formData.contact.part1}-${formData.contact.part2}-${formData.contact.part3}`,
        fundBirth: formData.birthDate,
        fundType: "ITEM",
        fundItem: formData.goods,
        fundNote: formData.notes,
        fundCheck: fundCheckStatus,
      };

      const response = await api.post("/funds/request", requestData);

      if (response.status === 200 || response.status === 201) {
        navigate("/funds/goods-details", {
          state: { formData: response.data },
        });
      }
    } catch (error) {
      console.error("후원 물품 신청 중 오류 발생:", error);
      setMessage("신청 중 오류가 발생했습니다. 다시 시도해주세요.");
      setTimeout(() => setMessage(null), 3000);
    }
  };

  return (
    <div>
      <div>
        <h3>후원물품 신청서</h3>

        <form onSubmit={handleSubmit}>
          <div className="form_wrap">
            <table className="table type2 responsive border">
              <colgroup>
                <col className="w30p" />
                <col />
              </colgroup>
              <tbody>
                <tr>
                  <th scope="row">신청자명</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        type="text"
                        id="applicantName"
                        name="applicantName"
                        value={formData.applicantName}
                        onChange={handleChange}
                        className="temp_input"
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">연락처</th>
                  <td className="phone_form">
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part1}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part1: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="3" // 최대 입력 길이 제한
                      />
                    </div>
                    <span>-</span>
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part2}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part2: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="4" // 최대 입력 길이 제한
                      />
                    </div>
                    <span>-</span>
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part3}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part3: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="4" // 최대 입력 길이 제한
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">생년월일</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        className="temp_input"
                        type="date"
                        id="birthDate"
                        value={formData.birthDate}
                        name="birthDate"
                        onChange={handleChange}
                      />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row">후원확인서 필 여부</th>
                  <div className="temp_form_box">
                    <td>
                      <div className="temp_form md w40p">
                        <select
                          id="confirmationRequired"
                          name="confirmationRequired"
                          value={formData.confirmationRequired}
                          onChange={handleChange}
                          className="temp_select"
                        >
                          <option value="">선택</option>
                          <option value="필요">필요</option>
                          <option value="불필요">불필요</option>
                        </select>
                      </div>
                    </td>
                  </div>
                </tr>

                <tr>
                  <th scope="row">후원물품</th>
                  <td>
                    <div className="form-input-item">
                      <textarea
                        id="goods"
                        name="goods"
                        value={formData.goods}
                        onChange={handleChange}
                        className="form-textarea w40p"
                        rows="3"
                        placeholder="후원물품 기부 시 작성"
                      ></textarea>
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">비고</th>
                  <td>
                    <div className="form-input-item">
                      <textarea
                        id="notes"
                        name="notes"
                        value={formData.notes}
                        onChange={handleChange}
                        className="form-textarea w40p"
                        rows="3"
                        placeholder="비고 입력"
                      ></textarea>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div className="form_center_box">
            <div className="form_btn_box">
              <div>
                <button
                  type="button"
                  onClick={() => navigate("/funds")}
                  className="form-button-secondary"
                >
                  이전
                </button>
              </div>
            </div>
            <div className="form_btn_box">
              <div>
                <button type="submit" className="form-button-primary">
                  다음
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>

      {message && <div className="form-message">{message}</div>}
    </div>
  );
};

// 후원 물품 신청 내역 컴포넌트
const GoodsApplicationDetails = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const responseData = location.state?.formData;

  if (!responseData) {
    return (
      <div className="application-details-error">
        <p>잘못된 접근입니다. 후원물품 신청서를 먼저 작성해주세요.</p>
        <button
          onClick={() => navigate("/funds/goods")}
          className="form-button-primary mt-4"
        >
          신청서로 이동
        </button>
      </div>
    );
  }

  const confirmationText = responseData.fundCheck === "Y" ? "필요" : "불필요";

  return (
    <div>
      <div>
        <h3>후원금 신청 내역</h3>
        <div className="form_wrap">
          <table className="table type2 responsive border">
            <colgroup>
              <col className="w30p" />
              <col />
            </colgroup>
            <tbody>
              <tr>
                <th scope="row">신청자명</th>
                <td>
                  <div className="form_desc">{responseData.fundSponsor}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">연락처</th>
                <td>
                  <div className="form_desc">{responseData.fundPhone}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">생년월일</th>
                <td>
                  <div className="form_desc">{responseData.fundBirth}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">후원확인서 필 여부</th>
                <td>
                  <div className="form_desc">{confirmationText}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">후원 물품</th>
                <td>
                  <div className="form_desc">{responseData.fundItem}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">비고</th>
                <td>
                  <div className="form_desc">
                    {responseData.fundNote || "없음"}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div className="form_center_box">
          <div className="form_btn_box">
            <div>
              <button
                onClick={() => navigate("/funds/donation")}
                className="form-button-secondary"
              >
                이전
              </button>
            </div>
          </div>
          <div className="form_btn_box">
            <div>
              <button
                onClick={() => navigate("/funds")}
                className="form-button-primary"
              >
                메인으로 이동
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

// 정기후원 신청서 컴포넌트
const RegularApplicationForm = () => {
  const navigate = useNavigate();
  const [message, setMessage] = useState(null);
  const [formData, setFormData] = useState({
    applicantName: "",
    contact: { part1: "010", part2: "", part3: "" },
    birthDate: "",
    confirmationRequired: "",
    fundAmount: "",
    bankName: "",
    accountNumber: "",
    accountHolder: "",
    withdrawalDay: "",
    notes: "",
  });

  const [isAmountInputDisabled, setIsAmountInputDisabled] = useState(true);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "contact") {
      const parts = value.split("-");
      setFormData((prev) => ({
        ...prev,
        contact: {
          part1: parts[0] || "",
          part2: parts[1] || "",
          part3: parts[2] || "",
        },
      }));
    } else if (name === "fundAmountSelect") {
      if (value === "직접 입력") {
        setIsAmountInputDisabled(false);
        setFormData((prev) => ({ ...prev, fundAmount: "" }));
      } else {
        setIsAmountInputDisabled(true);
        setFormData((prev) => ({ ...prev, fundAmount: value }));
      }
    } else {
      setFormData((prev) => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // 1. 로컬 스토리지에서 memberNum 가져오기
    const memberNum = localStorage.getItem("memberNum");
    const memberId = memberNum ? Number(memberNum) : null;

    // 2. 필수 입력 체크
    if (
      !formData.applicantName ||
      !formData.contact.part1 ||
      !formData.contact.part2 ||
      !formData.contact.part3 ||
      !formData.birthDate ||
      !formData.confirmationRequired ||
      !formData.fundAmount ||
      !formData.bankName ||
      !formData.accountNumber ||
      !formData.accountHolder ||
      !formData.withdrawalDay
    ) {
      if (
        !memberId &&
        (!formData.applicantName ||
          !formData.contact.part2 ||
          !formData.contact.part3 ||
          !formData.birthDate)
      ) {
        setMessage("비회원 후원은 신청자 정보를 모두 입력해야 합니다.");
        setTimeout(() => setMessage(null), 3000);
        return;
      }
    }
    try {
      const fundCheckStatus =
        formData.confirmationRequired === "필요" ? "Y" : "N";
      // 🔹 출금일 처리: "말일"은 99, 숫자일 경우 parseInt
      let withdrawalDayValue;
      if (formData.withdrawalDay === "말일") {
        withdrawalDayValue = 99;
      } else {
        // "10일" -> 10
        withdrawalDayValue = parseInt(
          formData.withdrawalDay.replace("일", ""),
          10
        );
      }

      const requestData = {
        memberId: memberId,
        fundSponsor: formData.applicantName,
        fundPhone: `${formData.contact.part1}-${formData.contact.part2}-${formData.contact.part3}`,
        fundBirth: formData.birthDate,
        fundType: "MONEY",
        fundMoney: Number(formData.fundAmount),
        fundBank: formData.bankName,
        fundAccountNum: formData.accountNumber,
        fundDepositor: formData.accountHolder,
        fundDrawlDate: withdrawalDayValue, // 숫자 값 전달
        fundNote: formData.notes,
        fundCheck: fundCheckStatus,
      };

      const response = await api.post("/funds/request", requestData);

      if (response.status === 200 || response.status === 201) {
        navigate("/funds/regular-details", {
          state: { formData: response.data },
        });
      }
    } catch (error) {
      console.error("정기후원 신청 중 오류 발생:", error);
      setMessage("신청 중 오류가 발생했습니다. 다시 시도해주세요.");
      setTimeout(() => setMessage(null), 3000);
    }
  };

  return (
    <div>
      <div>
        <h3>정기후원 신청서</h3>

        <form onSubmit={handleSubmit}>
          <div className="form_wrap">
            <table className="table type2 responsive border">
              <colgroup>
                <col className="w30p" />
                <col />
              </colgroup>
              <tbody>
                <tr>
                  <th scope="row">신청자명</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        type="text"
                        id="applicantName"
                        name="applicantName"
                        value={formData.applicantName}
                        onChange={handleChange}
                        className="temp_input"
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">연락처</th>
                  <td className="phone_form">
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part1}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part1: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="3" // 최대 입력 길이 제한
                      />
                    </div>
                    <span>-</span>
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part2}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part2: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="4" // 최대 입력 길이 제한
                      />
                    </div>
                    <span>-</span>
                    <div className="temp_form md w15p">
                      <input
                        type="text"
                        value={formData.contact.part3}
                        onChange={(e) => {
                          const onlyNumbers = e.target.value.replace(/\D/g, ""); // 숫자만 남김
                          setFormData((p) => ({
                            ...p,
                            contact: { ...p.contact, part3: onlyNumbers },
                          }));
                        }}
                        className="temp_input"
                        maxLength="4" // 최대 입력 길이 제한
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">생년월일</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        className="temp_input"
                        type="date"
                        id="birthDate"
                        value={formData.birthDate}
                        name="birthDate"
                        onChange={handleChange}
                      />
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row">후원확인서 필 여부</th>
                  <div className="temp_form_box">
                    <td>
                      <div className="temp_form md w40p">
                        <select
                          id="confirmationRequired"
                          name="confirmationRequired"
                          value={formData.confirmationRequired}
                          onChange={handleChange}
                          className="temp_select"
                        >
                          <option value="">선택</option>
                          <option value="필요">필요</option>
                          <option value="불필요">불필요</option>
                        </select>
                      </div>
                    </td>
                  </div>
                </tr>
                <tr>
                  <th scope="row">후원 금액</th>

                  <td>
                    <div className="all_day">
                      <select
                        name="fundAmountSelect"
                        onChange={handleChange}
                        className="form-select w30p"
                      >
                        <option value="">후원 금액 선택</option>
                        <option value="10000">10,000원</option>
                        <option value="20000">20,000원</option>
                        <option value="30000">30,000원</option>
                        <option value="직접 입력">직접 입력</option>
                      </select>
                      <div className="temp_form md w30p">
                        <input
                          type="text"
                          id="fundAmount"
                          name="fundAmount"
                          value={formData.fundAmount}
                          onChange={handleChange}
                          disabled={isAmountInputDisabled}
                          className="temp_input"
                        />
                      </div>
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">은행 선택</th>
                  <td>
                    <select
                      id="bankName"
                      name="bankName"
                      value={formData.bankName}
                      onChange={handleChange}
                      className="form-select"
                    >
                      <option value="">은행 선택</option>
                      <option value="국민은행">국민은행</option>
                      <option value="신한은행">신한은행</option>
                      <option value="우리은행">우리은행</option>
                      <option value="하나은행">하나은행</option>
                      <option value="기업은행">기업은행</option>
                    </select>
                  </td>
                </tr>

                <tr>
                  <th scope="row">계좌번호</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        type="text"
                        id="accountNumber"
                        name="accountNumber"
                        value={formData.accountNumber}
                        onChange={handleChange}
                        className="temp_input"
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">예금주명</th>
                  <td>
                    <div className="temp_form md w40p">
                      <input
                        type="text"
                        id="accountHolder"
                        name="accountHolder"
                        value={formData.accountHolder}
                        onChange={handleChange}
                        className="temp_input"
                      />
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">출금일</th>
                  <td>
                    <select
                      id="withdrawalDay"
                      name="withdrawalDay"
                      value={formData.withdrawalDay}
                      onChange={handleChange}
                      className="form-select"
                    >
                      <option value="">출금일 선택</option>
                      <option value="5일">5일</option>
                      <option value="10일">10일</option>
                      <option value="15일">15일</option>
                      <option value="20일">20일</option>
                      <option value="25일">25일</option>
                      <option value="말일">말일</option>
                    </select>
                  </td>
                </tr>
                <tr></tr>

                <tr>
                  <th scope="row">비고</th>
                  <td>
                    <div className="form-input-item">
                      <textarea
                        id="notes"
                        name="notes"
                        value={formData.notes}
                        onChange={handleChange}
                        className="form-textarea w40p"
                        rows="3"
                        placeholder="비고 입력"
                      ></textarea>
                    </div>
                  </td>
                </tr>

                <tr>
                  <th scope="row">정기후원 안내사항</th>
                  <td>
                    <ul className="form-notice-list">
                      <li>
                        가입 첫 달은 5, 10, 15, 20, 25일, 말일 중 가장 가까운
                        날에 출금됩니다.
                      </li>
                      <li>
                        정기 출금 실패 시 10, 15, 20, 25일, 말일에 재출금됩니다.
                      </li>
                    </ul>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div className="form_center_box">
            <div className="form_btn_box">
              <div>
                <button
                  type="button"
                  onClick={() => navigate("/funds")}
                  className="form-button-secondary"
                >
                  이전
                </button>
              </div>
            </div>
            <div className="form_btn_box">
              <div>
                <button type="submit" className="form-button-primary">
                  다음
                </button>
              </div>
            </div>
          </div>
        </form>
      </div>

      {message && <div className="form-message">{message}</div>}
    </div>
  );
};

// 정기후원 신청 내역 컴포넌트
const RegularApplicationDetails = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const responseData = location.state?.formData;

  if (!responseData) {
    return (
      <div className="application-details-error">
        <p>잘못된 접근입니다. 정기후원 신청서를 먼저 작성해주세요.</p>
        <button
          onClick={() => navigate("/funds/regular")}
          className="form-button-primary mt-4"
        >
          신청서로 이동
        </button>
      </div>
    );
  }

  const confirmationText = responseData.fundCheck === "Y" ? "필요" : "불필요";

  return (
    <div>
      <div>
        <h3>후원금 신청 내역</h3>
        <div className="form_wrap">
          <table className="table type2 responsive border">
            <colgroup>
              <col className="w30p" />
              <col />
            </colgroup>
            <tbody>
              <tr>
                <th scope="row">신청자명</th>
                <td>
                  <div className="form_desc">{responseData.fundSponsor}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">연락처</th>
                <td>
                  <div className="form_desc">{responseData.fundPhone}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">생년월일</th>
                <td>
                  <div className="form_desc">{responseData.fundBirth}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">후원확인서 필 여부</th>
                <td>
                  <div className="form_desc">{confirmationText}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">후원 금액</th>
                <td>
                  <div className="form_desc">
                    {responseData.fundMoney.toLocaleString()} 원
                  </div>
                </td>
              </tr>

              <tr>
                <th scope="row">은행</th>
                <td>
                  <div className="form_desc">{responseData.fundBank}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">계좌번호</th>
                <td>
                  <div className="form_desc">{responseData.fundAccountNum}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">예금주명</th>
                <td>
                  <div className="form_desc">{responseData.fundDepositor}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">출금일</th>
                <td>
                  <div className="form_desc">{responseData.fundDrawlDate}</div>
                </td>
              </tr>

              <tr>
                <th scope="row">비고</th>
                <td>
                  <div className="form_desc">
                    {responseData.fundNote || "없음"}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <div className="form_center_box">
          <div className="form_btn_box">
            <div>
              <button
                onClick={() => navigate("/funds/donation")}
                className="form-button-secondary"
              >
                이전
              </button>
            </div>
          </div>
          <div className="form_btn_box">
            <div>
              <button
                onClick={() => navigate("/funds")}
                className="form-button-primary"
              >
                메인으로 이동
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const MemberFundList = () => {
  const navigate = useNavigate();
  const [funds, setFunds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [currentPage, setCurrentPage] = useState(0); // 현재 페이지 (0부터 시작)
  const [totalPages, setTotalPages] = useState(0); // 총 페이지 수

  const isFetching = useRef(false);

  const fetchFunds = async (page) => {
    if (isFetching.current) return;

    setLoading(true);
    setError(null);
    isFetching.current = true;

    try {
      const res = await api.get(`/funds/list?page=${page}&size=10`);
      setFunds(res.data.content);
      setTotalPages(res.data.totalPages);
      setCurrentPage(res.data.pageable.pageNumber);
    } catch (err) {
      console.error("후원 목록 불러오기 오류:", err);
      if (err.response && err.response.status === 401) {
        setError("로그인이 필요합니다. 로그인 후 다시 시도해주세요.");
      } else {
        setError("후원 목록을 불러오는 데 실패했습니다.");
      }
    } finally {
      setLoading(false);
      isFetching.current = false;
    }
  };

  useEffect(() => {
    fetchFunds(currentPage);
  }, [currentPage]); // currentPage가 변경될 때마다 데이터 재조회

  const handleRowClick = (fundId) => {
    navigate(`/member/funds/${fundId}`);
  };

  // 기존 handlePageChange 함수를 남겨두어 재사용성을 높였습니다.
  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) {
      setCurrentPage(page);
    }
  };

  if (loading) {
    return (
      <div className="fund-list-container">
        <p>후원 목록을 불러오는 중입니다...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="fund-list-container">
        <p className="error-message">{error}</p>
        <button
          onClick={() => navigate("/login")}
          className="form-button-primary mt-4"
        >
          로그인 페이지로 이동
        </button>
      </div>
    );
  }

  return (
    <div>
      <h3>후원 목록</h3>
      <div>
        <table className="table type2 responsive border">
          <thead>
            <tr>
              <th>후원자명</th>
              <th>후원금/물품</th>
              <th>후원일</th>
            </tr>
          </thead>
          <tbody className="text_center">
            {funds.length > 0 ? (
              funds.map((fund) => (
                <tr
                  key={fund.fundId}
                  onClick={() => handleRowClick(fund.fundId)}
                  style={{ cursor: "pointer" }}
                >
                  <td>{fund.fundSponsor}</td>
                  <td>
                    {fund.fundMoney
                      ? `${fund.fundMoney.toLocaleString()} 원`
                      : fund.fundItem}
                  </td>
                  <td>{new Date(fund.fundTime).toLocaleString()}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="3">후원 목록이 없습니다.</td>
              </tr>
            )}
          </tbody>
        </table>

        {/* 수정된 페이지네이션 UI */}
        {totalPages > 1 && (
          <div className="pagination_box">
            <button
              className="page_btn prev"
              disabled={currentPage === 0}
              onClick={() => handlePageChange(currentPage - 1)}
            >
              이전
            </button>
            <div className="page_btn_box">
              {Array.from({ length: totalPages }, (_, i) => (
                <button
                  key={i}
                  className={`page ${currentPage === i ? "active" : ""}`}
                  onClick={() => handlePageChange(i)}
                >
                  {i + 1}
                </button>
              ))}
            </div>
            <button
              className="page_btn next"
              disabled={currentPage === totalPages - 1}
              onClick={() => handlePageChange(currentPage + 1)}
            >
              다음
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

const MemberFundDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [fundDetail, setFundDetail] = useState(null);
  const [message, setMessage] = useState(null);

  const authAxios = api.create({
    baseURL: "http://127.0.0.1:8090/",
    headers: { Authorization: `Bearer ${localStorage.getItem("accessToken")}` },
  });

  const fetchFundDetail = async () => {
    try {
      const res = await authAxios.get(`/funds/${id}`);
      setFundDetail(res.data);
    } catch (err) {
      console.error(err);
      setMessage("상세 정보 불러오기 실패");
    }
  };

  useEffect(() => {
    fetchFundDetail();
  }, [id]);

  if (!fundDetail) return <div>{message || "로딩 중..."}</div>;

  return (
    <div>
      <h3>후원 상세 정보</h3>
      <div className="form_wrap">
        <table className="table type2 responsive border">
          <colgroup>
            <col className="w20p" />
            <col />
          </colgroup>
          <tbody>
            <tr>
              <th>후원금/물품</th>
              <td>
                {fundDetail.fundMoney
                  ? `${fundDetail.fundMoney.toLocaleString()} 원`
                  : fundDetail.fundItem}
              </td>
            </tr>
            <tr>
              <th>신청자</th>
              <td>{fundDetail.fundSponsor}</td>
            </tr>
            <tr>
              <th>연락처</th>
              <td>{fundDetail.fundPhone}</td>
            </tr>
            <tr>
              <th>생년월일</th>
              <td>{fundDetail.fundBirth}</td>
            </tr>
            <tr>
              <th>후원일</th>
              <td>{fundDetail.fundTime}</td>
            </tr>
            <tr>
              <th>입금정보</th>
              <td>
                {fundDetail.fundBank} / {fundDetail.fundAccountNum} (
                {fundDetail.fundDepositor})
              </td>
            </tr>
            <tr>
              <th>인출예정일</th>
              <td>{fundDetail.fundDrawlDate}</td>
            </tr>
            <tr>
              <th>확인여부</th>
              <td>{fundDetail.fundCheck}</td>
            </tr>
            <tr>
              <th>비고</th>
              <td>{fundDetail.fundNote}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div className="form_center_box">
        <div className="temp_btn white md">
          <button type="button" className="btn" onClick={() => navigate(-1)}>
            목록보기
          </button>
        </div>
      </div>
    </div>
  );
};

const AdminFundList = () => {
  const navigate = useNavigate();
  const [funds, setFunds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 페이지네이션을 위한 상태 추가
  const [currentPage, setCurrentPage] = useState(0); // 현재 페이지 (0부터 시작)
  const [totalPages, setTotalPages] = useState(0); // 총 페이지 수

  // 중복 API 호출을 막기 위한 useRef
  const isFetching = useRef(false);

  // 페이지네이션을 포함한 API 호출 함수
  const fetchAdminFunds = async (page) => {
    // 이미 API 호출 중이면 함수 종료
    if (isFetching.current) return;

    setLoading(true);
    setError(null);
    isFetching.current = true;

    try {
      // API 호출 시 page와 size를 쿼리 파라미터로 전달
      const res = await api.get(`/funds/list?page=${page}&size=10`);
      setFunds(res.data.content);
      setTotalPages(res.data.totalPages);
      setCurrentPage(res.data.pageable.pageNumber);
    } catch (err) {
      console.error("관리자 후원 목록 불러오기 오류:", err);
      if (err.response && err.response.status === 403) {
        setError("접근 권한이 없습니다. 관리자 계정으로 로그인해주세요.");
      } else if (err.response && err.response.status === 401) {
        setError("로그인이 필요합니다. 로그인 페이지로 이동합니다.");
        setTimeout(() => navigate("/login"), 3000);
      } else {
        setError("후원 목록을 불러오는 데 실패했습니다.");
      }
    } finally {
      setLoading(false);
      isFetching.current = false;
    }
  };

  useEffect(() => {
    fetchAdminFunds(currentPage); // 컴포넌트 마운트 시 첫 페이지 데이터 불러오기
  }, [currentPage]); // currentPage가 변경될 때마다 데이터 재조회

  // 행 클릭 시 상세 페이지로 이동하는 함수
  const handleRowClick = (fundId) => {
    navigate(`/admin/funds/detail/${fundId}`);
  };

  // 페이지 번호 클릭 핸들러
  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) {
      setCurrentPage(page);
    }
  };

  if (loading) {
    return (
      <div className="fund-list-container">
        <p>후원 목록을 불러오는 중입니다...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="fund-list-container">
        <p className="error-message">{error}</p>
        {error.includes("로그인") && (
          <button
            onClick={() => navigate("/login")}
            className="form-button-primary mt-4"
          >
            로그인 페이지로 이동
          </button>
        )}
      </div>
    );
  }

  return (
    <div>
      <h3>후원 정보 관리</h3>
      <div className="form_wrap">
        <table className="table type2 responsive border">
          <thead>
            <tr>
              <th>신청자</th>
              <th>후원금/물품</th>
              <th>후원일</th>
            </tr>
          </thead>
          <tbody className="text_center">
            {funds.length > 0 ? (
              funds.map((fund) => (
                <tr
                  key={fund.fundId}
                  onClick={() => handleRowClick(fund.fundId)}
                  style={{ cursor: "pointer" }}
                >
                  <td>{fund.fundSponsor}</td>
                  <td>
                    {fund.fundMoney
                      ? `${fund.fundMoney.toLocaleString()} 원`
                      : fund.fundItem}
                  </td>
                  <td>{new Date(fund.fundTime).toLocaleString()}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="3">등록된 후원 내역이 없습니다.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* 수정된 페이지네이션 UI 추가 */}
      {totalPages > 1 && (
        <div className="pagination_box">
          <button
            className="page_btn prev"
            disabled={currentPage === 0}
            onClick={() => handlePageChange(currentPage - 1)}
          >
            이전
          </button>
          <div className="page_btn_box">
            {Array.from({ length: totalPages }, (_, i) => (
              <button
                key={i}
                className={`page ${currentPage === i ? "active" : ""}`}
                onClick={() => handlePageChange(i)}
              >
                {i + 1}
              </button>
            ))}
          </div>
          <button
            className="page_btn next"
            disabled={currentPage === totalPages - 1}
            onClick={() => handlePageChange(currentPage + 1)}
          >
            다음
          </button>
        </div>
      )}
    </div>
  );
};

const AdminFundDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [fundDetail, setFundDetail] = useState(null);
  const [message, setMessage] = useState("로딩 중...");

  useEffect(() => {
    const fetchFundDetail = async () => {
      try {
        const res = await api.get(`/funds/${id}`);
        setFundDetail(res.data);
      } catch (err) {
        console.error(err);
        if (err.response && err.response.status === 404) {
          setMessage("해당 후원 내역을 찾을 수 없습니다.");
        } else if (err.response && err.response.status === 403) {
          setMessage("접근 권한이 없습니다. 관리자 계정으로 로그인해주세요.");
        } else {
          setMessage("상세 정보 불러오기 실패");
        }
      }
    };
    fetchFundDetail();
  }, [id]);

  const handleUpdate = () => {
    // TODO: 수정 페이지로 이동 로직 구현
    // navigate(`/admin/funds/update/${id}`);
    alert("수정 기능은 아직 구현되지 않았습니다.");
  };

  const handleDelete = async () => {
    if (window.confirm("정말로 이 후원 내역을 삭제하시겠습니까?")) {
      try {
        await api.delete(`/funds/${id}`);
        alert("후원 내역이 성공적으로 삭제되었습니다.");
        // 삭제 후 목록 페이지로 이동
        navigate("/admin/funds/list");
      } catch (err) {
        console.error("후원 내역 삭제 실패:", err);
        alert("후원 내역 삭제에 실패했습니다.");
      }
    }
  };

  if (!fundDetail) return <div>{message}</div>;

  return (
    <div className="fund-detail-page">
      <h3>후원 상세 정보 (관리자)</h3>
      <div className="form_wrap">
        <table className="table type2 responsive border">
          <colgroup>
            <col className="w20p" />
            <col />
          </colgroup>
          <tbody>
            <tr>
              <th>후원금/물품</th>
              <td>
                {fundDetail.fundMoney
                  ? `${fundDetail.fundMoney.toLocaleString()} 원`
                  : fundDetail.fundItem}
              </td>
            </tr>
            <tr>
              <th>신청자</th>
              <td>{fundDetail.fundSponsor}</td>
            </tr>
            <tr>
              <th>연락처</th>
              <td>{fundDetail.fundPhone}</td>
            </tr>
            <tr>
              <th>생년월일</th>
              <td>{fundDetail.fundBirth}</td>
            </tr>
            <tr>
              <th>후원일</th>
              <td>{fundDetail.fundTime}</td>
            </tr>
            <tr>
              <th>입금 정보</th>
              <td>
                {fundDetail.fundBank} / {fundDetail.fundAccountNum} (
                {fundDetail.fundDepositor})
              </td>
            </tr>
            <tr>
              <th>인출 예정일</th>
              <td>{fundDetail.fundDrawlDate}</td>
            </tr>
            <tr>
              <th>확인 여부</th>
              <td>{fundDetail.fundCheck ? "확인됨" : "미확인"}</td>
            </tr>
            <tr>
              <th>비고</th>
              <td>{fundDetail.fundNote}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <div className="form_center_box">
        <div className="temp_btn white md">
          <button type="button" className="btn" onClick={() => navigate(-1)}>
            목록보기
          </button>
        </div>
        <div className="temp_btn md">
          <button type="button" className="btn" onClick={handleDelete}>
            삭제
          </button>
        </div>
      </div>
    </div>
  );
};

// 명명된 내보내기를 사용하여 각 컴포넌트를 내보냄
export {
  FundApplicationDetails,
  FundApplicationForm,
  FundMainPage,
  GoodsApplicationDetails,
  GoodsApplicationForm,
  RegularApplicationDetails,
  RegularApplicationForm,
  MemberFundList,
  MemberFundDetail,
  AdminFundList,
  AdminFundDetail,
};
