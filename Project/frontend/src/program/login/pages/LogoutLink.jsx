import { useAuth } from '../../../common/context/AuthContext';
import { useNavigate } from 'react-router-dom';

const LogoutLink = () => {
  const { logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout(); // 상태 false + localStorage 삭제
    navigate('/');
  };

  return <button onClick={handleLogout}>로그아웃</button>;
};

export default LogoutLink;
