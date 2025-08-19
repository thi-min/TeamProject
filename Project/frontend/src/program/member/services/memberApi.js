// 목적: "아이디 찾기 / 비밀번호 찾기" HTTP 호출 모듈
// - 컨트롤러가 /member 같은 프리픽스를 쓰지 않는다고 하셔서 API_PREFIX = "" 로 설정
// - 필요 시 한 줄만 바꿔서 경로를 /member, /auth 등으로 전환 가능
// - fetch 사용 (외부 라이브러리 불필요), 서버는 "문자열" 응답을 반환한다고 가정

// ──────────────────────────────────────────────────────────
// ✅ 백엔드가 루트(/)에 매핑된 경우: 빈 문자열("")
//    예) GET  /find-id
//        POST /find-pw
// ✅ 만약 이후에 /member 나 /auth 로 묶게 되면 아래 한 줄만 수정:
//    const API_PREFIX = "/member";  // 또는 "/auth"
// ──────────────────────────────────────────────────────────
import api from "../../../common/api/axios";

const API_PREFIX = ""; // 지금 요구사항에 맞춰 프리픽스 제거
// 공통 헤더 (JSON 요청에 사용)
function pickErrorMessage(
  error,
  fallback = "요청 처리 중 오류가 발생했습니다."
) {
  // axios error 형태 안전 접근
  const resp = error?.response;
  if (!resp) return fallback;

  // 서버가 문자열을 내려주는 경우(data가 string)
  if (typeof resp.data === "string" && resp.data.trim()) {
    return resp.data;
  }

  // JSON 형태 가정 시
  if (resp.data?.message) return resp.data.message;

  return fallback;
}

/**
 * 서버 에러 메시지 추출 유틸
 * - 서버가 순수 문자열로 에러 바디를 내려줘도 안전하게 처리
 * - JSON이 오더라도 text() 기반으로 수용
 */
async function readErrorMessage(resp) {
  try {
    const text = await resp.text();
    return text || "요청 처리 중 오류가 발생했습니다.";
  } catch {
    return "요청 처리 중 오류가 발생했습니다.";
  }
}
/**
 * 아이디 찾기
 * - GET /find-id?memberName=...&memberPhone=...
 * - 성공: "회원님의 ID는 OOOO 입니다." (문자열)
 * - 실패: 4xx + "일치하는 회원이 없습니다." (문자열)
 */
export async function apiFindMemberId({ memberName, memberPhone }) {
  try {
    const res = await api.get(`${API_PREFIX}/find-id`, {
      params: {
        memberName: memberName?.trim() ?? "",
        memberPhone: memberPhone?.trim() ?? "",
      },
      // 백엔드가 text/plain 반환 → 문자열로 받기
      responseType: "text",
      transformResponse: [(data) => data], // axios의 기본 JSON 파싱 비활성화
    });
    // res.data가 곧 서버의 문자열
    return res.data;
  } catch (error) {
    throw new Error(
      pickErrorMessage(error, "아이디 찾기 중 오류가 발생했습니다.")
    );
  }
}

/**
 * 비밀번호 찾기(본인확인)
 * - POST /find-pw
 * - 바디: { memberId, memberName, memberPhone }
 * - 성공: "본인 확인이 완료되었습니다. 비밀번호를 재설정 해주세요" (문자열)
 */
export async function apiFindMemberPw({ memberId, memberName, memberPhone }) {
  try {
    const res = await api.post(
      `${API_PREFIX}/find-pw`,
      {
        memberId: memberId?.trim() ?? "",
        memberName: memberName?.trim() ?? "",
        memberPhone: memberPhone?.trim() ?? "",
      },
      {
        responseType: "text",
        transformResponse: [(data) => data],
      }
    );
    return res.data;
  } catch (error) {
    throw new Error(
      pickErrorMessage(error, "비밀번호 찾기 중 오류가 발생했습니다.")
    );
  }
}
/**
 * 🔐 비밀번호 변경
 * - 백엔드: @PutMapping("/update-password") 가정
 * - 바디 DTO: { memberId, currentPassword, newPassword, newPasswordCheck }
 * - 성공: "비밀번호가 변경되었습니다." (문자열)
 * - 주의: 이 파일은 axios 인스턴스 이름이 'api' 이므로 'client'가 아니라 'api'를 사용해야 함
 */
export async function updatePassword({
  memberId,
  currentPassword,
  newPassword,
  newPasswordCheck,
}) {
  return api.put(
    `${API_PREFIX}/update-password`,
    {
      memberId: memberId?.trim() ?? "",
      currentPassword: currentPassword ?? "",
      newPassword: newPassword ?? "",
      newPasswordCheck: newPasswordCheck ?? "",
    },
    {
      responseType: "text",
      transformResponse: [(data) => data],
    }
  );
}
/**
 * 마이페이지 조회
 * GET /member/mypage
 * 성공: MemberMyPageResponseDto
 * 실패:
 *  - 401: 비로그인 → 로그인 페이지로 유도
 *  - 403: 비번 만료 → 비번 변경 페이지로 유도
 *  - 404: 회원 없음 → 에러 표시
 */
export async function apiGetMyPage() {
  return api.get(`${API_PREFIX}/member/mypage`);
}
/**
 * 마이페이지 주소 변경
 * payload: { postcode, roadAddress, detailAddress, memberAddress } 중 하나
 * 서버가 한 문자열(memberAddress)만 받으면 compose해서 memberAddress로 전송
 */
export async function apiUpdateMyAddress(payload) {
  // 서버가 memberAddress 한 필드만 받는다고 가정
  const { postcode = "", roadAddress = "", detailAddress = "" } = payload;
  const memberAddress =
    (postcode ? `[${postcode}] ` : "") +
    (roadAddress || "").trim() +
    (detailAddress ? ` ${detailAddress.trim()}` : "");

  // 예시 엔드포인트: PUT /member/mypage/address
  return api.put(`/member/mypage/address`, { memberAddress });
}
/**
 * 아이디 중복체크
 * @param {string} memberId 이메일(아이디)
 * @returns {{available:boolean, message:string}}
 */
export async function apiCheckDuplicateId(memberId) {
  const id = String(memberId ?? "").trim();
  try {
    const res = await api.get(`${API_PREFIX}/check-id`, {
      params: { memberId: id },
      // 서버가 text/plain 으로 줄 수도 있으니 JSON 고집 X
      transformResponse: [
        (data) => {
          // axios 기본 JSON 파싱 실패 시 data는 문자열
          try {
            return JSON.parse(data);
          } catch {
            return data;
          }
        },
      ],
      validateStatus: () => true, // 상태코드를 우리가 직접 해석
    });

    // ---- 상태코드 우선 해석 ----
    if (res.status === 409) {
      return {
        available: false,
        message:
          res.data?.message || res.data || "이미 사용 중인 아이디입니다.",
      };
    }
    if (res.status >= 400) {
      const msg =
        res.data?.message || res.data || "중복체크 중 오류가 발생했습니다.";
      throw new Error(String(msg));
    }

    // ---- 응답 바디 방어적 파싱 ----
    const body = res.data;
    // 1) { available: true/false }
    if (body && typeof body === "object" && "available" in body) {
      return {
        available: !!body.available,
        message:
          body.message ||
          (body.available
            ? "사용 가능한 아이디입니다."
            : "이미 사용 중인 아이디입니다."),
      };
    }
    // 2) { exists: true/false } → available = !exists
    if (body && typeof body === "object" && "exists" in body) {
      const available = !body.exists;
      return {
        available,
        message:
          body.message ||
          (available
            ? "사용 가능한 아이디입니다."
            : "이미 사용 중인 아이디입니다."),
      };
    }
    // 3) 문자열 응답
    if (typeof body === "string") {
      const s = body.toLowerCase();
      if (s.includes("가능") || s.includes("available") || s === "ok") {
        return { available: true, message: "사용 가능한 아이디입니다." };
      }
      if (
        s.includes("중복") ||
        s.includes("exists") ||
        s.includes("duplicate") ||
        s.includes("used")
      ) {
        return { available: false, message: "이미 사용 중인 아이디입니다." };
      }
      // 의미 모호 → 안전하게 안내
      return { available: true, message: body || "사용 가능한 아이디입니다." };
    }

    // 형태를 모르겠으면 안전 기본값
    return { available: true, message: "사용 가능한 아이디입니다." };
  } catch (error) {
    const msg = error?.message || "중복체크 중 오류가 발생했습니다.";
    throw new Error(msg);
  }
}
