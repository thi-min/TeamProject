// frontend/src/common/api/adminPwService.js
// 목적: 관리자 비밀번호 변경 API 호출을 공통 axios 인스턴스(api)로만 보내도록 통일

// ✅ (1) alias가 잡혀 있다면
// import api from "@/common/api/axios";

// ✅ (2) alias가 없다면 (권장: 실제 경로로 바꿔주세요)
import api from "../../../common/api/axios"; // ← 프로젝트 구조에 맞게 상대경로 조정

/**
 * 관리자 비밀번호 변경
 * @param {{ currentPassword: string, newPassword: string, newPasswordCheck: string, adminId?: string }} dto
 * @returns {Promise<string>} 성공 메시지(문자열)
 */
export async function updateAdminPassword(dto) {
  const res = await api.put("/admin/updatePw", dto);
  const data = res.data;
  if (typeof data === "string") return data;
  if (data?.message) return data.message;
  return "비밀번호가 성공적으로 변경되었습니다.";
}
