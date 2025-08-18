import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
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
      path: '/fund/money'
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
      path: '/fund/recurring'
    },
  ];

  return (
    <div className="fund-main-page">
      <div className="fund-main-container">
        <h1 className="fund-main-title">후원 파트</h1>
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

  const handleNext = () => {
    if (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate || !formData.confirmationRequired || !formData.fundAmount) {
      setMessage('필수 입력 항목을 모두 작성해주세요.');
      setTimeout(() => setMessage(null), 3000); 
      return;
    }
    navigate('/fund/money-details', { state: { formData } });
  };
  
  return (
    <div className="application-form-page">
      <div className="application-form-container">
        <h2 className="application-form-title">후원금 신청서</h2>
        
        <div className="form-input-group">
          <div className="form-input-item">
            <label htmlFor="applicantName" className="form-label required">신청자명</label>
            <input type="text" id="applicantName" name="applicantName" value={formData.applicantName} onChange={handleChange} className="form-input" />
          </div>
          
          <div className="form-input-item">
            <label htmlFor="contact" className="form-label required">연락처</label>
            <div className="form-contact-input">
              <input type="text" name="contact" value={formData.contact.part1} onChange={(e) => handleChange({target: {name: 'contact', value: `${e.target.value}-${formData.contact.part2}-${formData.contact.part3}`}})} className="form-input text-center" />
              <span>-</span>
              <input type="text" name="contact" value={formData.contact.part2} onChange={(e) => handleChange({target: {name: 'contact', value: `${formData.contact.part1}-${e.target.value}-${formData.contact.part3}`}})} className="form-input text-center" />
              <span>-</span>
              <input type="text" name="contact" value={formData.contact.part3} onChange={(e) => handleChange({target: {name: 'contact', value: `${formData.contact.part1}-${formData.contact.part2}-${e.target.value}`}})} className="form-input text-center" />
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
          <button
            onClick={() => navigate('/fund')}
            className="form-button-secondary"
          >
            이전
          </button>
          <button
            onClick={handleNext}
            className="form-button-primary"
          >
            다음
          </button>
        </div>
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
  const formData = location.state?.formData;

  const formatContact = (contact) => `${contact.part1}-${contact.part2}-${contact.part3}`;
  
  if (!formData) {
    return (
      <div className="application-details-error">
        <p>잘못된 접근입니다. 후원금 신청서를 먼저 작성해주세요.</p>
        <button onClick={() => navigate('/fund/money')} className="form-button-primary mt-4">신청서로 이동</button>
      </div>
    );
  }
  
  return (
    <div className="application-details-page">
      <div className="application-details-container">
        <h2 className="application-details-title">후원금 신청 내역</h2>
        
        <div className="details-info-group">
          <div className="details-info-item">
            <div className="details-label">신청자명</div>
            <div className="details-text">{formData.applicantName}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">연락처</div>
            <div className="details-text">{formatContact(formData.contact)}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">생년월일</div>
            <div className="details-text">{formData.birthDate}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원확인서 필 여부</div>
            <div className="details-text">{formData.confirmationRequired}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원 금액</div>
            <div className="details-text">{formData.fundAmount.toLocaleString()} 원</div>
          </div>
          
          <div className="details-info-item-textarea">
            <div className="details-label">비고</div>
            <div className="details-text-notes">{formData.notes || '없음'}</div>
          </div>
        </div>
        
        <div className="details-buttons">
          <button
            onClick={() => navigate('/fund/money')}
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

  const handleNext = () => {
    if (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate || !formData.confirmationRequired || !formData.goods) {
      setMessage('필수 입력 항목을 모두 작성해주세요.');
      setTimeout(() => setMessage(null), 3000); 
      return;
    }
    navigate('/fund/goods-details', { state: { formData } });
  };
  
  return (
    <div className="application-form-page">
      <div className="application-form-container">
        <h2 className="application-form-title">후원 물품 신청서</h2>
        
        <div className="form-input-group">
          <div className="form-input-item">
            <label htmlFor="applicantName" className="form-label required">신청자명</label>
            <input type="text" id="applicantName" name="applicantName" value={formData.applicantName} onChange={handleChange} className="form-input" />
          </div>
          
          <div className="form-input-item">
            <label htmlFor="contact" className="form-label required">연락처</label>
            <div className="form-contact-input">
              <input type="text" name="contact" value={formData.contact.part1} onChange={(e) => handleChange({target: {name: 'contact', value: `${e.target.value}-${formData.contact.part2}-${formData.contact.part3}`}})} className="form-input text-center" />
              <span>-</span>
              <input type="text" name="contact" value={formData.contact.part2} onChange={(e) => handleChange({target: {name: 'contact', value: `${formData.contact.part1}-${e.target.value}-${formData.contact.part3}`}})} className="form-input text-center" />
              <span>-</span>
              <input type="text" name="contact" value={formData.contact.part3} onChange={(e) => handleChange({target: {name: 'contact', value: `${formData.contact.part1}-${formData.contact.part2}-${e.target.value}`}})} className="form-input text-center" />
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
            onClick={handleNext}
            className="form-button-primary"
          >
            다음
          </button>
        </div>
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
  const formData = location.state?.formData;
  
  const formatContact = (contact) => `${contact.part1}-${contact.part2}-${contact.part3}`;

  if (!formData) {
    return (
      <div className="application-details-error">
        <p>잘못된 접근입니다. 후원물품 신청서를 먼저 작성해주세요.</p>
        <button onClick={() => navigate('/fund/goods')} className="form-button-primary mt-4">신청서로 이동</button>
      </div>
    );
  }
  
  return (
    <div className="application-details-page">
      <div className="application-details-container">
        <h2 className="application-details-title">후원 물품 신청 내역</h2>
        
        <div className="details-info-group">
          <div className="details-info-item">
            <div className="details-label">신청자명</div>
            <div className="details-text">{formData.applicantName}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">연락처</div>
            <div className="details-text">{formatContact(formData.contact)}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">생년월일</div>
            <div className="details-text">{formData.birthDate}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원확인서 필 여부</div>
            <div className="details-text">{formData.confirmationRequired}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원 물품</div>
            <div className="details-text">{formData.goods}</div>
          </div>
          
          <div className="details-info-item-textarea">
            <div className="details-label">비고</div>
            <div className="details-text-notes">{formData.notes || '없음'}</div>
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
  );
};

// 정기후원 신청서 컴포넌트
const RecurringApplicationForm = () => {
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

  const handleNext = () => {
    if (!formData.applicantName || !formData.contact.part2 || !formData.contact.part3 || !formData.birthDate || !formData.confirmationRequired || !formData.fundAmount || !formData.bankName || !formData.accountNumber || !formData.accountHolder || !formData.withdrawalDay) {
      setMessage('필수 입력 항목을 모두 작성해주세요.');
      setTimeout(() => setMessage(null), 3000); 
      return;
    }
    navigate('/fund/recurring-details', { state: { formData } });
  };

  return (
    <div className="application-form-page">
      <div className="application-form-container">
        <h2 className="application-form-title">정기후원 신청서</h2>
        
        <div className="form-input-group">
          <div className="form-input-item">
            <label htmlFor="applicantName" className="form-label required">신청자명</label>
            <input type="text" id="applicantName" name="applicantName" value={formData.applicantName} onChange={handleChange} className="form-input" />
          </div>
          
          <div className="form-input-item">
            <label htmlFor="contact" className="form-label required">연락처</label>
            <div className="form-contact-input">
              <input type="text" name="contact" value={formData.contact.part1} onChange={(e) => handleChange({target: {name: 'contact', value: `${e.target.value}-${formData.contact.part2}-${formData.contact.part3}`}})} className="form-input text-center" />
              <span>-</span>
              <input type="text" name="contact" value={formData.contact.part2} onChange={(e) => handleChange({target: {name: 'contact', value: `${formData.contact.part1}-${e.target.value}-${formData.contact.part3}`}})} className="form-input text-center" />
              <span>-</span>
              <input type="text" name="contact" value={formData.contact.part3} onChange={(e) => handleChange({target: {name: 'contact', value: `${formData.contact.part1}-${formData.contact.part2}-${e.target.value}`}})} className="form-input text-center" />
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
            onClick={handleNext}
            className="form-button-primary"
          >
            결제하기
          </button>
        </div>
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
const RecurringApplicationDetails = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const formData = location.state?.formData;
  
  const formatContact = (contact) => `${contact.part1}-${contact.part2}-${contact.part3}`;

  if (!formData) {
    return (
      <div className="application-details-error">
        <p>잘못된 접근입니다. 정기후원 신청서를 먼저 작성해주세요.</p>
        <button onClick={() => navigate('/fund/recurring')} className="form-button-primary mt-4">신청서로 이동</button>
      </div>
    );
  }

  return (
    <div className="application-details-page">
      <div className="application-details-container">
        <h2 className="application-details-title">정기후원 신청 내역</h2>

        <div className="details-info-group">
          <div className="details-info-item">
            <div className="details-label">신청자명</div>
            <div className="details-text">{formData.applicantName}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">연락처</div>
            <div className="details-text">{formatContact(formData.contact)}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">생년월일</div>
            <div className="details-text">{formData.birthDate}</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">후원확인서 필 여부</div>
            <div className="details-text">{formData.confirmationRequired}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">후원 금액</div>
            <div className="details-text">{formData.fundAmount.toLocaleString()} 원</div>
          </div>
          
          <div className="details-info-item">
            <div className="details-label">은행</div>
            <div className="details-text">{formData.bankName}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">계좌번호</div>
            <div className="details-text">{formData.accountNumber}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">예금주명</div>
            <div className="details-text">{formData.accountHolder}</div>
          </div>

          <div className="details-info-item">
            <div className="details-label">출금일</div>
            <div className="details-text">{formData.withdrawalDay}</div>
          </div>
          
          <div className="details-info-item-textarea">
            <div className="details-label">비고</div>
            <div className="details-text-notes">{formData.notes || '없음'}</div>
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

// 명명된 내보내기를 사용하여 각 컴포넌트를 내보냄
export { FundApplicationDetails, FundApplicationForm, FundMainPage, GoodsApplicationDetails, GoodsApplicationForm, RecurringApplicationDetails, RecurringApplicationForm };

