import { Link } from 'react-router-dom';
import { routeAccessMap } from '../routes/routeAccessMap';
import { useAuth } from '../context/AuthContext';


//메뉴 랜더링
const HeaderNav = () => {
  const { userRole } = useAuth(); //현재 사용자 역할

  return (
    <nav id="depth">
      <ul className="depth_list clearfix">
        {routeAccessMap
          .filter(route =>
            route.access === 'ALL' ||
            (userRole === 'USER' && route.access === 'USER') ||
            (userRole === 'ADMIN' && route.access === 'ADMIN')
          )
          .map((route, index) => (
            <li className="depth1_item" key={index}>
              <Link to={route.path}>{route.title}</Link>
            </li>
          ))}
      </ul>
    </nav>
  );
};

export default HeaderNav;
