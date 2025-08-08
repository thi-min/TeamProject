// 📁 HeaderNav.jsx 또는 NavLinks.jsx
import { Link } from "react-router-dom";
import menuRoutes from "../common/routes/menuRoutes";
import { useContext } from "react";
import AuthContext from "../common/context/AuthContext";

const HeaderNav = () => {
  const { auth } = useContext(AuthContext);
  const role = auth?.role || "ANONYMOUS";

  return (
    <nav className="nav_box">
      <div className="depth_area">
        {menuRoutes.map((menu) => {
          // 권한 필터링
          if (menu.access !== "ALL" && menu.access !== role) return null;

          // 단일 메뉴 (1차 메뉴만 있는 경우)
          if (menu.path) {
            return (
              <Link key={menu.title} to={menu.path}>
                {menu.title}
              </Link>
            );
          }

          // 하위 메뉴가 있는 경우 (2차 메뉴)
          return (
            <div key={menu.title} className="has-children">
              <span>{menu.title}</span>
              <div className="submenu">
                {menu.children?.map((sub) => {
                  if (sub.access !== "ALL" && sub.access !== role) return null;
                  return (
                    <Link key={sub.path} to={sub.path}>
                      {sub.title}
                    </Link>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>
    </nav>
  );
};

export default HeaderNav;
