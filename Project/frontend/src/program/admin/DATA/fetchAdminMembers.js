import api from "@/common/api/axios";

/**
 * 공용 훅(usePagedSearch)의 fetcher 시그니처에 맞춘 구현
 * @param {{ page:number, size:number, keyword:string }} args
 * keyword: 인풋에서 받은 문자열. 여기서는 memberNum로 사용.
 */
export default async function fetchAdminMembersByNum({ page, size, keyword }) {
  // [수정①] 엔드포인트
  const URL = "/admin/membersList"; // ⬅️ 필요 시 다른 URL로 변경

  // [수정②] 검색 파라미터 매핑
  // memberNum은 숫자여야 하므로, 숫자 아님 → 필터 미적용
  const trimmed = String(keyword ?? "").trim();
  const memberNum =
    trimmed !== "" && /^\d+$/.test(trimmed) ? Number(trimmed) : undefined;

  const res = await api.get(URL, {
    params: {
      page,
      size,
      // 백엔드가 기대하는 파라미터 이름에 맞춰 전송
      // 예) camelCase: memberNum / snake_case: member_num
      memberNum: memberNum, // ⬅️ 백엔드가 member_num이면 바꾸세요: member_num: memberNumParam
    },
  });

  const data = res?.data ?? {};

  // [수정③] 응답 래퍼 처리 (Page<T> 그대로 vs bbsList 래퍼)
  const pageObj = Array.isArray(data?.content) ? data : data?.bbsList ?? {};

  // [수정④] 아이템 매핑 (서버 필드명 → 화면에서 쓰기 편한 키)
  const items = (pageObj?.content ?? []).map((m) => ({
    // 서버가 snake_case라면 아래 유지
    memberNum: m.member_num,
    memberName: m.member_name,
    memberId: m.member_id,
    memberDay: m.member_day,
    memberState: m.member_state,
    memberSex: m.member_sex,

    // 서버가 camelCase면 위 대신 아래로 교체
    // memberNum:  m.memberNum,
    // memberName: m.memberName,
    // memberId:   m.memberId,
    // memberDay:  m.memberDay,
    // memberState:m.memberState,
    // memberSex:  m.memberSex,
  }));

  return {
    items,
    page: Number.isInteger(pageObj?.number) ? pageObj.number : page,
    totalPages: pageObj?.totalPages ?? 0,
  };
}
