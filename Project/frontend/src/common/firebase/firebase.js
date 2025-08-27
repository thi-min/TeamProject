// src/common/firebase/firebase.js
// 목적: Firebase 앱/인증(auth) 초기화 파일
// - v9 모듈러 SDK 사용
// - Phone 인증에 필요한 getAuth 만 export
// - auth.languageCode = 'ko' 로 한국어 SMS/리캡챠 UX 지정

import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

// ✅ 사용자 제공 설정값 (개발용은 그대로 사용 가능, 운영 배포 시 .env로 분리 권장)
const firebaseConfig = {
  apiKey: "AIzaSyCJiTgVzqOtNQJPdSaZvEDJCMhBGD2kMpE",
  authDomain: "dogproject-80c14.firebaseapp.com",
  projectId: "dogproject-80c14",
  storageBucket: "dogproject-80c14.firebasestorage.app",
  messagingSenderId: "226758401134",
  appId: "1:226758401134:web:afb2f1e02e46619ac59a7f",
  measurementId: "G-F5PCGM4XW4",
};

// ✅ Firebase 앱 초기화
const app = initializeApp(firebaseConfig);

// ✅ Auth 인스턴스 생성 (Phone 인증에 사용)
export const auth = getAuth(app);

// 한국어로 리캡챠/문구 표시
auth.languageCode = "ko";

// 주의:
// - Firebase Authentication > Sign-in method 에서 "Phone" 제공자를 반드시 "활성화"해야 함
// - Authentication > Settings > Authorized domains 에 개발 도메인 추가: localhost, 127.0.0.1 등
