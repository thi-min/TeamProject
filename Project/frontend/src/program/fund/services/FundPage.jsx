// import axios from 'axios';
import { api } from "../../../common/api/axios.js";
import { useState , useEffect} from 'react';
import { useLocation, useNavigate,useParams } from 'react-router-dom';
import '../style/Fund.css'; // 경로 수정

// 후원 섹션 컴포넌트
const FundSection = ({ title, description, icon, onDonateClick }) => {
  return (
    <div className="fund-section-card">
      <div className="fund-section-icon">{icon}</div>
      <h3 className="fund-section-title">{title}</h3>
      <p className="fund-section-description">{description}</p>
      <button 
        className="fund-donate-button"
        onClick={onDonateClick}
      >
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
      title: '후원금',
      description: '금전적 지원을 통해 다양한 프로젝트와 활동에 도움을 줄 수 있습니다. 소중한 후원금은 투명하게 사용됩니다.',
      icon: '💸',
      path: '/fund/donation'
    },
    {
      title: '후원물품',
      description: '필요한 물품을 직접 후원하여 더 직접적이고 실질적인 도움을 전할 수 있습니다.',
      icon: '🎁',
      path: '/fund/goods'
    },
    {
      title: '정기후원',
      description: '정기적인 후원을 통해 지속가능한 지원과 안정적인 운영을 도모할 수 있습니다.',
      icon: '💖',
      path: '/fund/regular'
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
    applicantName: '',
    contact: { part1: '010', part2: '', part3: '' },
    birthDate: '',
    confirmationRequired: '',
    fundAmount: '',
    notes: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'contact') {
      const parts = value.split('-');
      setFormData(prev => ({
        ...prev,
        contact: {
          part1: parts[0] || '',
          part2: parts[1] || '',
          part3: parts[2] || ''
        }
      }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // 1. 로컬 스토리지에서 memberNum 가져오기
    const memberNum = localStorage.getItem("memberNum");
    const memberId = memberNum ? Number(memberNum) : null;

    if (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate || !formData.confirmationRequired || !formData.fundAmount) {
      // 2. 로그인 상태가 아닐 때만 유효성 검사 추가
      if (!memberId && (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate)) {
        setMessage('비회원 후원은 신청자 정보를 모두 입력해야 합니다.');
        setTimeout(() => setMessage(null), 3000); 
        return;
      }
    }
    
    try {
      const fundCheckStatus = formData.confirmationRequired === '필요' ? 'Y' : 'N';
      
      const requestData = {
        memberId: memberId, // 동적으로 설정된 memberId 사용
        fundSponsor: formData.applicantName,
        fundPhone: `${formData.contact.part1}-${formData.contact.part2}-${formData.contact.part3}`,
        fundBirth: formData.birthDate,
        fundType: 'REGULAR', 
        fundMoney: formData.fundAmount,
        fundNote: formData.notes,
        fundCheck: fundCheckStatus
      };

      const response = await api.post('/fund/request', requestData);

      if (response.status === 200 || response.status === 201) {
        navigate('/fund/donation-details', { state: { formData: response.data } });
      }

    } catch (error) {
      console.error("후원금 신청 중 오류 발생:", error);
      setMessage('신청 중 오류가 발생했습니다. 다시 시도해주세요.');
      setTimeout(() => setMessage(null), 3000);
    }
  };

return (
    <div className="application-form-page">
      <div className="application-form-container">
        <h2 className="application-form-title">후원금 신청서</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-input-group">
            <div className="form-input-item">
              <label htmlFor="applicantName" className="form-label required">신청자명</label>
              <input type="text" id="applicantName" name="applicantName" value={formData.applicantName} onChange={handleChange} className="form-input" />
            </div>
            
            <div className="form-input-item">
              <label htmlFor="contact" className="form-label required">연락처</label>
              <div className="form-contact-input">
                <input
                  type="text"
                  value={formData.contact.part1}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part1: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="3" // 최대 입력 길이 제한
                />
                <span>-</span>
                <input
                  type="text"
                  value={formData.contact.part2}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part2: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="4" // 최대 입력 길이 제한
                />
                <span>-</span>
                <input
                  type="text"
                  value={formData.contact.part3}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part3: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="4" // 최대 입력 길이 제한
                />
              </div>
            </div>
            
            <div className="form-input-item">
              <label htmlFor="birthDate" className="form-label required">생년월일</label>
              <input type="date" id="birthDate" name="birthDate" value={formData.birthDate} onChange={handleChange} className="form-input" />
            </div>
            
            <div className="form-input-item">
              <label htmlFor="confirmationRequired" className="form-label required">후원확인서 필 여부</label>
              <select id="confirmationRequired" name="confirmationRequired" value={formData.confirmationRequired} onChange={handleChange} className="form-select">
                <option value="">선택</option>
                <option value="필요">필요</option>
                <option value="불필요">불필요</option>
              </select>
            </div>
            
            <div className="form-input-item">
              <label htmlFor="fundAmount" className="form-label required">후원 금액</label>
              <div className="form-amount-input">
                <input type="number" id="fundAmount" name="fundAmount" value={formData.fundAmount} onChange={handleChange} className="form-input" />
                <span className="form-unit">원</span>
              </div>
            </div>
            
            <div className="form-input-item-textarea">
              <label htmlFor="notes" className="form-label">비고</label>
              <textarea id="notes" name="notes" value={formData.notes} onChange={handleChange} className="form-textarea" rows="3" placeholder="비고 입력"></textarea>
            </div>
          </div>

          <div className="form-buttons">
            <button onClick={() => navigate('/fund')} className="form-button-secondary">이전</button>
            <button type="submit" className="form-button-primary">다음</button>
          </div>
        </form>
      </div>
      {message && (
        <div className="form-message">
          {message}
        </div>
      )}
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
        <button onClick={() => navigate('/fund/donation')} className="form-button-primary mt-4">신청서로 이동</button>
      </div>
    );
  }

  // ⭐️ FundCheck 값에 따라 '필요' 또는 '불필요'로 변환
  const confirmationText = responseData.fundCheck === 'Y' ? '필요' : '불필요';

  return (
    <div className="application-details-page">
      <div className="application-details-container">
        <h2 className="application-details-title">후원금 신청 내역</h2>
        
        <div className="details-info-group">
          <div className="details-info-item">
            <div className="details-label">신청자명</div>
            <div className="details-text">{responseData.fundSponsor}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">연락처</div>
            <div className="details-text">{responseData.fundPhone}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">생년월일</div>
            <div className="details-text">{responseData.fundBirth}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원확인서 필 여부</div>
            {/* ⭐️ 변환된 값 출력 */}
            <div className="details-text">{confirmationText}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원 금액</div>
            <div className="details-text">{responseData.fundMoney.toLocaleString()} 원</div>
          </div>
          
          <div className="details-info-item-textarea">
            <div className="details-label">비고</div>
            <div className="details-text-notes">{responseData.fundNote || '없음'}</div>
          </div>
        </div>
        
        <div className="details-buttons">
          <button onClick={() => navigate('/fund/donation')} className="form-button-secondary">이전</button>
          <button onClick={() => navigate('/fund')} className="form-button-primary">메인으로 이동</button>
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
    applicantName: '',
    contact: { part1: '010', part2: '', part3: '' },
    birthDate: '',
    confirmationRequired: '', 
    goods: '',
    notes: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'contact') {
      const parts = value.split('-');
      setFormData(prev => ({
        ...prev,
        contact: {
          part1: parts[0] || '',
          part2: parts[1] || '',
          part3: parts[2] || ''
        }
      }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
// 1. 로컬 스토리지에서 memberNum 가져오기
    const memberNum = localStorage.getItem("memberNum");
    const memberId = memberNum ? Number(memberNum) : null;
    
    if (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate || !formData.confirmationRequired || !formData.goods) {
      // 2. 로그인 상태가 아닐 때만 유효성 검사 추가
      if (!memberId && (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate)) {
        setMessage('비회원 후원은 신청자 정보를 모두 입력해야 합니다.');
        setTimeout(() => setMessage(null), 3000); 
        return;
      }
    }
    
    try {
      const fundCheckStatus = formData.confirmationRequired === '필요' ? 'Y' : 'N';
      
      const requestData = {
        memberId: memberId, // 동적으로 설정된 memberId 사용
        fundSponsor: formData.applicantName,
        fundPhone: `${formData.contact.part1}-${formData.contact.part2}-${formData.contact.part3}`,
        fundBirth: formData.birthDate,
        fundType: 'ITEM', 
        fundItem: formData.goods,
        fundNote: formData.notes,
        fundCheck: fundCheckStatus
      };
      
      const response = await api.post('/fund/request', requestData);

      if (response.status === 200 || response.status === 201) {
        navigate('/fund/goods-details', { state: { formData: response.data } });
      }

    } catch (error) {
      console.error("후원 물품 신청 중 오류 발생:", error);
      setMessage('신청 중 오류가 발생했습니다. 다시 시도해주세요.');
      setTimeout(() => setMessage(null), 3000);
    }
  };
  
  return (
    <div className="application-form-page">
      <div className="application-form-container">
        <h2 className="application-form-title">후원 물품 신청서</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-input-group">
            <div className="form-input-item">
              <label htmlFor="applicantName" className="form-label required">신청자명</label>
              <input type="text" id="applicantName" name="applicantName" value={formData.applicantName} onChange={handleChange} className="form-input" />
            </div>
            
            <div className="form-input-item">
              <label htmlFor="contact" className="form-label required">연락처</label>
              <div className="form-contact-input">
                <input
                  type="text"
                  value={formData.contact.part1}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part1: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="3" // 최대 입력 길이 제한
                />
                <span>-</span>
                <input
                  type="text"
                  value={formData.contact.part2}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part2: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="4" // 최대 입력 길이 제한
                />
                <span>-</span>
                <input
                  type="text"
                  value={formData.contact.part3}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part3: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="4" // 최대 입력 길이 제한
                />
              </div>
          </div>
          <div className="form-input-item">
            <label htmlFor="birthDate" className="form-label required">생년월일</label>
            <input type="date" id="birthDate" name="birthDate" value={formData.birthDate} onChange={handleChange} className="form-input" />
          </div>
          
          <div className="form-input-item">
            <label htmlFor="confirmationRequired" className="form-label required">후원확인서 필 여부</label>
            <select id="confirmationRequired" name="confirmationRequired" value={formData.confirmationRequired} onChange={handleChange} className="form-select">
              <option value="">선택</option>
              <option value="필요">필요</option>
              <option value="불필요">불필요</option>
            </select>
          </div>
          
          <div className="form-input-item">
            <label htmlFor="goods" className="form-label required">후원물품</label>
            <input type="text" id="goods" name="goods" value={formData.goods} onChange={handleChange} className="form-input" placeholder="후원물품 기부 시 작성" />
          </div>
          
          <div className="form-input-item-textarea">
            <label htmlFor="notes" className="form-label">비고</label>
            <textarea id="notes" name="notes" value={formData.notes} onChange={handleChange} className="form-textarea" rows="3" placeholder="비고 입력"></textarea>
          </div>
        </div>

        <div className="form-buttons">
          <button
            onClick={() => navigate('/fund')}
            className="form-button-secondary"
          >
            이전
          </button>
          <button
            type="submit"
            className="form-button-primary"
          >
            다음
          </button>
        </div>
        </form>
      </div>
      {message && (
        <div className="form-message">
          {message}
        </div>
      )}
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
        <button onClick={() => navigate('/fund/goods')} className="form-button-primary mt-4">신청서로 이동</button>
      </div>
    );
  }
  
  const confirmationText = responseData.fundCheck === 'Y' ? '필요' : '불필요';

  return (
    <div className="application-details-page">
      <div className="application-details-container">
        <h2 className="application-details-title">후원 물품 신청 내역</h2>
        
        <div className="details-info-group">
          <div className="details-info-item">
            <div className="details-label">신청자명</div>
            <div className="details-text">{responseData.fundSponsor}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">연락처</div>
            <div className="details-text">{responseData.fundPhone}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">생년월일</div>
            <div className="details-text">{responseData.fundBirth}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원확인서 필 여부</div>
            <div className="details-text">{confirmationText}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원 물품</div>
            <div className="details-text">{responseData.fundItem}</div>
          </div>
          
          <div className="details-info-item-textarea">
            <div className="details-label">비고</div>
            <div className="details-text-notes">{responseData.fundNote || '없음'}</div>
          </div>
        </div>
        
        <div className="details-buttons">
          <button
            onClick={() => navigate('/fund/goods')}
            className="form-button-secondary"
          >
            이전
          </button>
          <button
            onClick={() => navigate('/fund')}
            className="form-button-primary"
          >
            메인으로 이동
          </button>
        </div>
        </div>

        </div>
      )}
    
  


// 정기후원 신청서 컴포넌트
const RegularApplicationForm = () => {
  const navigate = useNavigate();
  const [message, setMessage] = useState(null);
  const [formData, setFormData] = useState({
    applicantName: '',
    contact: { part1: '010', part2: '', part3: '' },
    birthDate: '',
    confirmationRequired: '',
    fundAmount: '',
    bankName: '',
    accountNumber: '',
    accountHolder: '',
    withdrawalDay: '',
    notes: ''
  });

  const [isAmountInputDisabled, setIsAmountInputDisabled] = useState(true);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === 'contact') {
      const parts = value.split('-');
      setFormData(prev => ({
        ...prev,
        contact: {
          part1: parts[0] || '',
          part2: parts[1] || '',
          part3: parts[2] || ''
        }
      }));
    } else if (name === 'fundAmountSelect') { 
      if (value === '직접 입력') {
        setIsAmountInputDisabled(false); 
        setFormData(prev => ({ ...prev, fundAmount: '' }));
      } else {
        setIsAmountInputDisabled(true); 
        setFormData(prev => ({ ...prev, fundAmount: value }));
      }
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

   // 1. 로컬 스토리지에서 memberNum 가져오기
    const memberNum = localStorage.getItem("memberNum");
    const memberId = memberNum ? Number(memberNum) : null;
    
     // 2. 필수 입력 체크
  if (
    !formData.applicantName || !formData.contact.part2 || !formData.contact.part3 ||
    !formData.birthDate || !formData.confirmationRequired || !formData.fundAmount ||
    !formData.bankName || !formData.accountNumber || !formData.accountHolder || !formData.withdrawalDay
  ) {
    if (!memberId && (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate)) {
      setMessage('비회원 후원은 신청자 정보를 모두 입력해야 합니다.');
      setTimeout(() => setMessage(null), 3000); 
      return;
    }
  }
    try {
      const fundCheckStatus = formData.confirmationRequired === '필요' ? 'Y' : 'N';
    // 🔹 출금일 처리: "말일"은 99, 숫자일 경우 parseInt
      let withdrawalDayValue;
      if (formData.withdrawalDay === "말일") {
        withdrawalDayValue = 99;
      } else {
        // "10일" -> 10
        withdrawalDayValue = parseInt(formData.withdrawalDay.replace("일", ""), 10);
      }
      
      const requestData = {
        memberId: memberId,
        fundSponsor: formData.applicantName,
        fundPhone: `${formData.contact.part1}-${formData.contact.part2}-${formData.contact.part3}`,
        fundBirth: formData.birthDate,
        fundType: 'MONEY',
        fundMoney: Number(formData.fundAmount),
        fundBank: formData.bankName,
        fundAccountNum: formData.accountNumber,
        fundDepositor: formData.accountHolder,
        fundDrawlDate: withdrawalDayValue, // 숫자 값 전달
        fundNote: formData.notes,
        fundCheck: fundCheckStatus
     };
      
      const response = await api.post('/fund/request', requestData);

      if (response.status === 200 || response.status === 201) {
        navigate('/fund/regular-details', { state: { formData: response.data } });
      }

    } catch (error) {
      console.error("정기후원 신청 중 오류 발생:", error);
      setMessage('신청 중 오류가 발생했습니다. 다시 시도해주세요.');
      setTimeout(() => setMessage(null), 3000);
    }
  };

  return (
    <div className="application-form-page">
      <div className="application-form-container">
        <h2 className="application-form-title">정기후원 신청서</h2>
        <form onSubmit={handleSubmit}>
        <div className="form-input-group">
          <div className="form-input-item">
            <label htmlFor="applicantName" className="form-label required">신청자명</label>
            <input type="text" id="applicantName" name="applicantName" value={formData.applicantName} onChange={handleChange} className="form-input" />
          </div>
          
          <div className="form-input-item">
            <label htmlFor="contact" className="form-label required">연락처</label>
            <div className="form-contact-input">
              <input
                  type="text"
                  value={formData.contact.part1}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part1: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="3" // 최대 입력 길이 제한
                />
                <span>-</span>
                <input
                  type="text"
                  value={formData.contact.part2}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part2: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="4" // 최대 입력 길이 제한
                />
                <span>-</span>
                <input
                  type="text"
                  value={formData.contact.part3}
                  onChange={(e) => setFormData(p => ({ ...p, contact: { ...p.contact, part3: e.target.value } }))}
                  className="form-input text-center"
                  maxLength="4" // 최대 입력 길이 제한
                />
                </div>
          </div>
          
          <div className="form-input-item">
            <label htmlFor="birthDate" className="form-label required">생년월일</label>
            <input type="date" id="birthDate" name="birthDate" value={formData.birthDate} onChange={handleChange} className="form-input" />
          </div>
          
          <div className="form-input-item">
            <label htmlFor="confirmationRequired" className="form-label required">후원확인서 필 여부</label>
            <select id="confirmationRequired" name="confirmationRequired" value={formData.confirmationRequired} onChange={handleChange} className="form-select">
              <option value="">선택</option>
              <option value="필요">필요</option>
              <option value="불필요">불필요</option>
            </select>
          </div>
          
          <div className="form-input-item">
            <label htmlFor="fundAmount" className="form-label required">후원 금액</label>
            <div className="form-amount-select-input">
              <select name="fundAmountSelect" onChange={handleChange} className="form-select w-1/2">
                <option value="">후원 금액 선택</option>
                <option value="10000">10,000원</option>
                <option value="20000">20,000원</option>
                <option value="30000">30,000원</option>
                <option value="직접 입력">직접 입력</option>
              </select>
              <input type="number" name="fundAmount" value={formData.fundAmount} onChange={handleChange} disabled={isAmountInputDisabled} className="form-input w-1/2" />
            </div>
          </div>

          <div className="form-input-item">
            <label htmlFor="bankName" className="form-label required">은행 선택</label>
            <select id="bankName" name="bankName" value={formData.bankName} onChange={handleChange} className="form-select">
              <option value="">은행 선택</option>
              <option value="국민은행">국민은행</option>
              <option value="신한은행">신한은행</option>
              <option value="우리은행">우리은행</option>
              <option value="하나은행">하나은행</option>
              <option value="기업은행">기업은행</option>
            </select>
          </div>

          <div className="form-input-item">
            <label htmlFor="accountNumber" className="form-label required">계좌번호</label>
            <input type="text" id="accountNumber" name="accountNumber" value={formData.accountNumber} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-input-item">
            <label htmlFor="accountHolder" className="form-label required">예금주명</label>
            <input type="text" id="accountHolder" name="accountHolder" value={formData.accountHolder} onChange={handleChange} className="form-input" />
          </div>

          <div className="form-input-item">
            <label htmlFor="withdrawalDay" className="form-label required">출금일</label>
            <select id="withdrawalDay" name="withdrawalDay" value={formData.withdrawalDay} onChange={handleChange} className="form-select">
              <option value="">출금일 선택</option>
              <option value="5일">5일</option>
              <option value="10일">10일</option>
              <option value="15일">15일</option>
              <option value="20일">20일</option>
              <option value="25일">25일</option>
              <option value="말일">말일</option>
            </select>
          </div>
          
          <div className="form-input-item-textarea">
            <label htmlFor="notes" className="form-label">비고</label>
            <textarea id="notes" name="notes" value={formData.notes} onChange={handleChange} className="form-textarea" rows="3" placeholder="비고 입력"></textarea>
          </div>
        </div>

        <div className="form-notice-box">
          <h3 className="form-notice-title">정기후원 안내사항</h3>
          <ul className="form-notice-list">
            <li>가입 첫 달은 5, 10, 15, 20, 25일, 말일 중 가장 가까운 날에 출금됩니다.</li>
            <li>정기 출금 실패 시 10, 15, 20, 25일, 말일에 재출금됩니다.</li>
          </ul>
        </div>
        <div className="form-checkbox-container">
          <input type="checkbox" id="confirmation" className="form-checkbox" />
          <label htmlFor="confirmation">위 내용을 확인하셨습니까?</label>
        </div>

        <div className="form-buttons right-aligned">
          <button
            onClick={() => navigate('/fund')}
            className="form-button-secondary"
          >
            이전
          </button>
          <button
            type="submit"
            className="form-button-primary"
          >
            결제하기
          </button>
        </div>
        </form>
      </div>
      {message && (
        <div className="form-message">
          {message}
        </div>
      )}
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
        <button onClick={() => navigate('/fund/regular')} className="form-button-primary mt-4">신청서로 이동</button>
      </div>
    );
  }

  const confirmationText = responseData.fundCheck === 'Y' ? '필요' : '불필요';

  return (
    <div className="application-details-page">
      <div className="application-details-container">
        <h2 className="application-details-title">정기후원 신청 내역</h2>

        <div className="details-info-group">
          <div className="details-info-item">
            <div className="details-label">신청자명</div>
            <div className="details-text">{responseData.fundSponsor}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">연락처</div>
            <div className="details-text">{responseData.fundPhone}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">생년월일</div>
            <div className="details-text">{responseData.fundBirth}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원확인서 필 여부</div>
            <div className="details-text">{confirmationText}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">후원 금액</div>
            <div className="details-text">{responseData.fundMoney.toLocaleString()} 원</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">은행</div>
            <div className="details-text">{responseData.fundBank}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">계좌번호</div>
            <div className="details-text">{responseData.fundAccountNum}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">예금주명</div>
            <div className="details-text">{responseData.fundDepositor}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">출금일</div>
            <div className="details-text">{responseData.fundDrawlDate}</div>
          </div>
          
          <div className="details-info-item-textarea">
            <div className="details-label">비고</div>
            <div className="details-text-notes">{responseData.fundNote || '없음'}</div>
          </div>
        </div>
        
        <div className="details-buttons right-aligned">
          <button
            onClick={() => navigate('/fund')}
            className="form-button-primary"
          >
            메인으로 이동
          </button>
        </div>
      </div>
    </div>
  );
};

const MemberFundList = () => {
    const navigate = useNavigate();
    const [funds, setFunds] = useState([]);
    const [message, setMessage] = useState(null);

    const authAxios = api.create({
        baseURL: 'http://localhost:8080/api',
        headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
    });

    const fetchFunds = async () => {
        try {
            const res = await authAxios.get('/funds'); // 전체 목록 조회
            setFunds(res.data);
        } catch (err) {
            console.error(err);
            setMessage("목록 불러오기 실패");
        }
    };

    useEffect(() => {
        fetchFunds();
    }, []);

    return (
        <div className="fund-list-page">
            <h2>나의 후원 목록</h2>
            <table className="fund-table">
                <thead>
                    <tr>
                        <th>제목</th>
                        <th>후원자</th>
                        <th>금액</th>
                        <th>상태</th>
                    </tr>
                </thead>
                <tbody>
                    {funds.map(fund => (
                        <tr key={fund.fundId} 
                            style={{ cursor: 'pointer' }}
                            onClick={() => navigate(`/member/fund/detail/${fund.fundId}`)}
                        >
                            <td>{fund.title}</td>
                            <td>{fund.memberName}</td>
                            <td>{fund.amount}</td>
                            <td>{fund.state}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
            {message && <div>{message}</div>}
        </div>
    );
};


const MemberFundDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [fundDetail, setFundDetail] = useState(null);
    const [message, setMessage] = useState(null);

    const authAxios = api.create({
        baseURL: 'http://localhost:8080/api',
        headers: { Authorization: `Bearer ${localStorage.getItem('accessToken')}` }
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
        <div className="fund-detail-page">
            <h2>후원 상세</h2>
            <div><strong>제목:</strong> {fundDetail.title}</div>
            <div><strong>후원자:</strong> {fundDetail.memberName}</div>
            <div><strong>금액:</strong> {fundDetail.amount}</div>
            <div><strong>상태:</strong> {fundDetail.state}</div>
            <button onClick={() => navigate(-1)}>목록으로 돌아가기</button>
        </div>
    );
};



// 명명된 내보내기를 사용하여 각 컴포넌트를 내보냄
export { FundApplicationDetails, FundApplicationForm, FundMainPage, GoodsApplicationDetails, GoodsApplicationForm, RegularApplicationDetails, RegularApplicationForm, MemberFundList, MemberFundDetail};