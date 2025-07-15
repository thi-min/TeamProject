#함께 마당 팀 프로젝트

+ [화면정의서](https://www.figma.com/design/nTh81nva0JrQA1IakEUY2y/%ED%95%A8%EA%B2%8C%EB%A7%88%EB%8B%B9-%ED%99%94%EB%A9%B4%EC%A0%95%EC%9D%98%EC%84%9C?node-id=27-73&t=uppo0fHdFbpJ5gqJ-0)
+ [기능정의서, 구현정의서, 메뉴구조도, DB구성 리스트](https://docs.google.com/spreadsheets/d/11IeV-mdxs4t-SV8Si9VfBWFi8TP4IR0em3ZXPzXKcso/edit?gid=0#gid=0)
+ [유스케이스](https://app.diagrams.net/#G1TyS2MtcFxZIYL2ozCjKoXumwz_kmENtK#%7B%22pageId%22%3A%22aozdoE-P7H4EPuOHVj0a%22%7D)

-----------------------------------------------------------------------------------------------------------------

#업무 분담

+ 김강민
  + 예약(봉사, 놀이터, 입양)

+ 유승주
  + 게시판(기본, 질문, 이미지) + 첨부파일 

+ 안형주
  + 보안
    + 로그인+카카오로그인
    + 휴대폰 인증
    + 회원가입
    + 계정 접근관련
    + 마이페이지

+ 이재복
  + 서비스기능
    + 지도 + 1:1 채팅 + 후원

+ 추천 작업 순서
  1. entity - DB 구조를 바탕으로 데이터 모델 정의
  2. dto - 	요청/응답용 데이터 객체 정의
  3. repository - DB 접근 계층 구현
  4. service - 비즈니스 로직 처리
  5. exception - 예외 처리 및 메시지 통일
  6. controller - 외부 요청 응답 처리
  7. config - 설정파일 (필요 시 WebMvc, Security 등)
