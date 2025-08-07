import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

//페이지 접근 제한
//보호된 라우트를 감싸는 컴포넌트
const PrivateRoute = ({ children, allowedRoles }) => {
  const { isLogin, userRole } = useAuth();

  if (!isLogin) return <Navigate to="/login" />;
  if (!allowedRoles.includes(userRole)) return <Navigate to="/403" />;

  return children;
};

export default PrivateRoute;
