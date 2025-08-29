// src/components/nav/w.jsx
// ✅ 헤더/사이드 어디서든 재사용 가능한 네비게이션 컴포넌트
// - 1차 메뉴: 항상 링크 + className="depth1_item"
// - 2차 메뉴: children 있으면 드롭다운으로 출력 + className="depth2_item"
// - 권한: access(ALL/USER/ADMIN) 를 role로 필터링

import { Link } from "react-router-dom";
import { useAuth } from "../../common/context/AuthContext"; // 전역 로그인 상태
import menuRoutes from "../../common/routes/menuRoutes"; // 메뉴 데이터

const canAccess = (access, isLogin, userRole) => {
  if (access === "ALL") return true; // 누구나
  if (access === "USER") return !!isLogin; // 로그인 사용자(ADMIN 포함)
  if (access === "ADMIN") return userRole === "ADMIN";
  return false;
};
const NavLinks = () => {
  // 컨텍스트에서 현재 로그인 여부와 역할을 가져옴
  const { isLogin, userRole } = useAuth();

  // 1차 메뉴 필터링
  const visibleMenus = (menuRoutes || []).filter((menu) =>
    canAccess(menu.access, isLogin, userRole)
  );

  return (
    <div className="nav_box">
      <div className="depth_area">
        <div className="depth_list">
          {visibleMenus.map((menu) => {
            const hasChildren =
              Array.isArray(menu.children) && menu.children.length > 0;

            // 2차 메뉴 필터
            const visibleChildren = hasChildren
              ? menu.children.filter((sub) =>
                  canAccess(sub.access, isLogin, userRole)
                )
              : [];

            // 1차 메뉴 path 없으면 2차 메뉴 첫 번째 path로 폴백
            const firstPath = menu.path || (visibleChildren[0]?.path ?? "#");

            return (
              <div key={menu.title} className="depth1_item">
                <Link to={firstPath}>{menu.title}</Link>

                {visibleChildren.length > 0 && (
                  <div className="depth2_item">
                    {visibleChildren.map((sub) => (
                      <Link key={sub.path} to={sub.path}>
                        {sub.title}
                      </Link>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};

export default NavLinks;
