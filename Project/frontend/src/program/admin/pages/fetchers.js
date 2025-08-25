import api from "../../../common/api/axios";

// 백엔드가 PageResponseDto(AdminMemberListResponseDto)로 통일해서 내려온다고 가정
export async function fetchAdminMembersByNum({ page, size, keyword }) {
  const trimmed = String(keyword ?? "").trim();
  const memberNum = /^\d+$/.test(trimmed) ? Number(trimmed) : undefined;

  const { data } = await api.get("/admin/membersList", {
    params: { page, size, memberNum }, // 백엔드: @ModelAttribute PageRequestDto 에 바인딩
  });

  // data = { content, page, size, totalPages, totalElements }
  const items = (data?.content ?? []).map((m) => ({
    memberNum: m.memberNum,
    memberName: m.memberName,
    memberId: m.memberId,
    memberDay: m.memberDay,
    memberState: m.memberState,
    memberLock: m.memberLock,
  }));

  return {
    items,
    page: data?.page ?? page,
    totalPages: data?.totalPages ?? 0,
  };
}
