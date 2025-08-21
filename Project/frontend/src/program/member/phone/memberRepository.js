// src/features/phone/memberRepository.js
// 목적: Firestore에서 회원 데이터 조회/중복체크 유틸
// 컬렉션 구조 예시: collection "members", docId = memberId (문자열)
// 필수 필드: { memberId: string, phone: string }

import { db } from "../../firebase/firebase";
import {
  collection,
  doc,
  getDoc,
  getDocs,
  query,
  where,
  limit,
} from "firebase/firestore";

const MEMBERS = "members";

// 전화번호로 회원 1명 조회 (정확 일치)
export async function findMemberByPhone(phoneDigitsOnly) {
  const q = query(
    collection(db, MEMBERS),
    where("phone", "==", phoneDigitsOnly),
    limit(1)
  );
  const snap = await getDocs(q);
  if (snap.empty) return null;
  const d = snap.docs[0];
  return { id: d.id, ...d.data() }; // {id: memberId, memberId, phone, ...}
}

// memberId로 회원 조회
export async function findMemberById(memberId) {
  const ref = doc(db, MEMBERS, memberId);
  const snap = await getDoc(ref);
  if (!snap.exists()) return null;
  return { id: snap.id, ...snap.data() };
}

// 전화번호 중복 여부 확인 (optional: 자기 자신 제외)
export async function isPhoneTaken(phoneDigitsOnly, excludeMemberId) {
  const owner = await findMemberByPhone(phoneDigitsOnly);
  if (!owner) return false;
  if (excludeMemberId && owner.id === excludeMemberId) return false; // 본인 번호면 중복 아님
  return true;
}
