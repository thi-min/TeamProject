import React from 'react';

const AdoptInfo = () => {
  return (
    <>
      <style>
        {`
          body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f0f0f0;
          }

          .container {
            max-width: 800px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border: 1px solid #ccc;
          }

          h1 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
          }

          .step {
            margin-bottom: 25px;
            padding: 15px;
            border: 1px solid #ddd;
            background-color: #fafafa;
          }

          .step h3 {
            color: #555;
            margin-top: 0;
            font-size: 18px;
          }

          .step p {
            margin: 10px 0;
            line-height: 1.5;
          }

          .step ul {
            margin: 10px 0;
            padding-left: 20px;
          }

          .step li {
            margin-bottom: 5px;
          }

          .notice {
            background-color: #ffffcc;
            border: 1px solid #ffcc00;
            padding: 15px;
            margin: 20px 0;
          }

          .notice h3 {
            color: #cc6600;
            margin-top: 0;
          }

          .contact {
            background-color: #e6f3ff;
            border: 1px solid #66ccff;
            padding: 15px;
            margin: 20px 0;
            text-align: center;
          }

          .contact h3 {
            color: #0066cc;
            margin-top: 0;
          }

          table {
            width: 100%;
            border-collapse: collapse;
            margin: 15px 0;
          }

          table, th, td {
            border: 1px solid #ccc;
          }

          th, td {
            padding: 8px;
            text-align: left;
          }

          th {
            background-color: #f0f0f0;
          }
        `}
      </style>
      <div className="container">
        <h1>🐕 강아지 입양 절차 안내 🐕</h1>

        <div className="step">
          <h3>1단계: 입양 신청</h3>
          <p>강아지를 입양하고 싶으시면 먼저 신청을 해주세요.</p>
          <ul>
            <li>전화나 이메일로 연락하기</li>
            <li>입양 신청서 작성하기</li>
            <li>원하는 강아지 종류나 크기 말하기</li>
          </ul>
        </div>

        <div className="step">
          <h3>2단계: 서류 준비</h3>
          <p>입양을 위해 필요한 서류들을 준비해주세요.</p>
          <ul>
            <li>신분증 사본</li>
            <li>주민등록등본</li>
            <li>가족 동의서 (같이 사는 가족이 있는 경우)</li>
          </ul>
        </div>

        <div className="step">
          <h3>3단계: 상담 받기</h3>
          <p>전문가와 상담을 통해 입양 준비가 되었는지 확인합니다.</p>
          <ul>
            <li>강아지 키우는 방법 배우기</li>
            <li>집 환경이 적합한지 확인하기</li>
            <li>입양 후 계획 이야기하기</li>
          </ul>
        </div>

        <div className="step">
          <h3>4단계: 강아지 만나기</h3>
          <p>입양할 강아지와 직접 만나서 서로 어울리는지 확인해보세요.</p>
          <ul>
            <li>강아지와 시간 보내기</li>
            <li>성격이나 특성 알아보기</li>
            <li>가족 모두 함께 만나보기</li>
          </ul>
        </div>

        <div className="step">
          <h3>5단계: 입양 완료</h3>
          <p>모든 절차가 끝나면 드디어 입양을 완료합니다!</p>
          <ul>
            <li>입양 계약서 쓰기</li>
            <li>강아지 건강 기록 받기</li>
            <li>강아지 집으로 데려가기</li>
          </ul>
        </div>

        <div className="notice">
          <h3>⚠️ 꼭 알아두세요!</h3>
          <p>
            <strong>입양은 평생 책임입니다.</strong> 강아지는 10년 이상 살아요. 끝까지 책임질 수 있는지 잘 생각해보세요.
          </p>
          <p>입양 후 1주일 안에는 마음이 바뀌면 다시 보낼 수 있어요. 하지만 강아지한테는 좋지 않으니까 신중하게 결정하세요.</p>
        </div>

        <div className="contact">
          <h3>📞 문의하기</h3>
          <p>
            <strong>전화:</strong> 02-1234-5678
          </p>
          <p>
            <strong>이메일:</strong> dog@adopt.com
          </p>
          <p>
            <strong>주소:</strong> 서울시 강남구 입양로 123
          </p>
          <p>
            <strong>운영시간:</strong> 월~금 9시-18시, 토 9시-15시
          </p>
        </div>

        <p style={{ textAlign: 'center', marginTop: '30px', color: '#666' }}>
          마지막 수정일: 2024년 12월 15일
        </p>
      </div>
    </>
  );
};

export default AdoptInfo;
