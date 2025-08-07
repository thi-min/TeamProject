import { Link } from "react-router-dom";
import { useContext } from "react";
import AuthContext from "../../common/context/AuthContext";
import menuRoutes from "../../router/menuRoutes";

const NavLinks = () => {
  const { auth } = useContext(AuthContext);
  const role = auth?.role || "ANONYMOUS";

  return (
    <nav>
      <ul>
        {menuRoutes.map((menu) => {
          if (menu.access !== "ALL" && menu.access !== role) return null;

          return (
            <li key={menu.title}>
              {menu.path ? (
                <Link to={menu.path}>{menu.title}</Link>
              ) : (
                <>
                  <span>{menu.title}</span>
                  <ul>
                    {menu.children?.map((child) =>
                      child.access === "ALL" || child.access === role ? (
                        <li key={child.path}>
                          <Link to={child.path}>{child.title}</Link>
                        </li>
                      ) : null
                    )}
                  </ul>
                </>
              )}
            </li>
          );
        })}
      </ul>
    </nav>
  );
};

export default NavLinks;