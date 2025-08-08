// ✅ 로그인 API 호출 함수
// - 응답 바디와 헤더(Authorization, X-Refresh-Token) 모두에서 토큰을 시도해서 뽑음
// - 초보자도 보이게 주석 잔뜩
export async function loginUser({ memberId, memberPw }) {
  const res = await fetch('/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    // 백엔드 스펙에 맞게 키 이름 유지
    body: JSON.stringify({ memberId, memberPw }),
  });

  // 바디 JSON 파싱 (여기엔 member, isPasswordExpired 같은 게 들어있던 로그 확인됨)
  const data = await res.json();

  // 1) 헤더에서 accessToken 시도: Authorization: Bearer xxx
  const authHeader = res.headers.get('Authorization') || res.headers.get('authorization');
  const headerAccess = authHeader?.startsWith('Bearer ')
    ? authHeader.replace(/^Bearer\s+/i, '')
    : null;

  // 2) 헤더에서 refreshToken 시도(예: X-Refresh-Token 커스텀 헤더)
  const headerRefresh = res.headers.get('X-Refresh-Token') || res.headers.get('x-refresh-token');

  // 3) 바디에서 시도(백엔드가 바디로 줄 수도 있으니 후보키 다 확인)
  const bodyAccess =
    data.accessToken ??
    data.token ??
    data.jwt ??
    data?.member?.accessToken ??
    null;

  const bodyRefresh =
    data.refreshToken ??
    data?.member?.refreshToken ??
    null;

  // 최종 토큰 결정(헤더 우선, 없으면 바디)
  const accessToken = headerAccess ?? bodyAccess ?? null;
  const refreshToken = headerRefresh ?? bodyRefresh ?? null;

  // 디버깅 로그(개발 중에만)
  console.log('[auth.js] res data:', data);
  console.log('[auth.js] parsed tokens:', { accessToken, refreshToken });

  if (!res.ok) {
    // 실패면 에러 던져서 상위에서 catch
    throw new Error(data?.message || '로그인 실패');
  }

  // 👉 LoginPage에서 바로 쓰기 쉽게 토큰과 바디를 함께 반환
  return { accessToken, refreshToken, data };
}
