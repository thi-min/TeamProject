// src/program/admin/services/adminApi.js
// 목적: 관리자 회원 관리 API (백엔드 매핑과 1:1)
// 백엔드 컨트롤러:
//   @RequestMapping("/admin")
//   GET  /admin/membersList
//   GET  /admin/membersList/{memberNum}
//   PUT  /admin/membersList/{memberNum}?memberState=...&memberLock=...

import api from "../../../common/api/axios";

/**
 * (임시) 페이지네이션 없이 한 번에 목록 가져오기
 * - 백엔드가 PageResponseDto를 반환하므로, 여기서 content만 뽑아서 배열로 반환
 * - size는 충분히 크게(예: 10,000) 설정해서 한 번에 가져옴
 * - keyword가 있으면 서버 검색 사용 (백엔드 findByMemberNameContaining)
 */
export async function fetchAdminMembersNoPaging({ keyword = "" } = {}) {
  const { data } = await api.get("/admin/membersList", {
    params: { page: 1, size: 10000, keyword },
  });
  // 서버가 PageResponseDto면 data.content, 혹시 배열로 직접 내려줄 수도 있으니 둘 다 대응
  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  return [];
}

/** 회원 상세 조회 */
export async function fetchAdminMemberDetail(memberNum) {
  // ✅ 공백 없이 정확한 경로
  const { data } = await api.get(`/admin/membersList/${memberNum}`);
  return data;
}

/** 회원 상태/잠금 수정 (쿼리 파라미터 방식) */
export async function updateAdminMemberState(memberNum, { memberState, memberLock }) {
  const { data } = await api.put(
    `/admin/membersList/${memberNum}`,
    null,
    { params: { memberState, memberLock } }
  );
  return data;
}
