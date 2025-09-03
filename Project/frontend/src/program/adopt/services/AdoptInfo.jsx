import React from 'react';

const AdoptInfo = () => {
  return (
    <>
      <style>
        {`
          .step_item + .step_item{margin-top:30px;}

        `}
      </style>
      <div className="cts7_wrap">
        <h3>강아지 입양 절차 안내</h3>
        <div class="box">
          <div className="step_item">
            <span class="box_title">1단계 입양 신청</span>
            <span class="box_desc">강아지를 입양하고 싶으시면 먼저 신청을 해주세요.</span>
            <ul class="bu">
              <li>전화나 이메일로 연락하기</li>
              <li>입양 신청서 작성하기</li>
              <li>원하는 강아지 종류나 크기 말하기</li>
            </ul>
          </div>
          <div className="step_item">
            <span class="box_title">2단계 서류 준비</span>
            <span class="box_desc">입양을 위해 필요한 서류들을 준비해주세요.</span>
            <ul class="bu">
              <li>신분증 사본</li>
              <li>주민등록등본</li>
              <li>가족 동의서 (같이 사는 가족이 있는 경우)</li>
            </ul>
          </div>

          <div className="step_item">
            <span class="box_title">3단계: 상담 받기</span>
            <span class="box_desc">전문가와 상담을 통해 입양 준비가 되었는지 확인합니다.</span>
            <ul class="bu">
              <li>강아지 키우는 방법 배우기</li>
              <li>집 환경이 적합한지 확인하기</li>
              <li>입양 후 계획 이야기하기</li>
            </ul>
          </div>

          <div className="step_item">
            <span class="box_title">4단계: 강아지 만나기</span>
            <span class="box_desc">입양할 강아지와 직접 만나서 서로 어울리는지 확인해보세요.</span>
            <ul class="bu">
              <li>강아지와 시간 보내기</li>
              <li>성격이나 특성 알아보기</li>
              <li>가족 모두 함께 만나보기</li>
            </ul>
          </div>

          <div className="step_item">
            <span class="box_title">5단계: 입양 완료</span>
            <span class="box_desc">모든 절차가 끝나면 드디어 입양을 완료합니다!</span>
            <ul class="bu">
              <li>입양 계약서 쓰기</li>
              <li>강아지 건강 기록 받기</li>
              <li>강아지 집으로 데려가기</li>
            </ul>
          </div>
        </div>

        <div className="warning_box type2">
          <h3 style={{ marginTop: '5px' }}>⚠️ 꼭 알아두세요!</h3>
          <p>
            <strong>입양은 평생 책임입니다.</strong> 강아지는 10년 이상 살아요. 끝까지 책임질 수 있는지 잘 생각해보세요.
          </p>
          <p>입양 후 1주일 안에는 마음이 바뀌면 다시 보낼 수 있어요. 하지만 강아지한테는 좋지 않으니까 신중하게 결정하세요.</p>
        </div>

        <div className="warning_box type4">
          <h3 style={{ marginTop: '5px' }}>📞 문의하기</h3>
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
          마지막 수정일: 2025년 9월 3일
        </p>

      </div >

    </>
  );
};

export default AdoptInfo;
