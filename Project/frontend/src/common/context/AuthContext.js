import { createContext, useContext, useState, useEffect } from 'react';
import {jwtDecode} from 'jwt-decode'; //설치 필요: npm install jwt-decode

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isLogin, setIsLogin] = useState(false);
  const [userRole, setUserRole] = useState(null); //사용자 역할 상태 추가

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      setIsLogin(true);

      // accessToken 디코딩해서 role 추출
      try {
        const decoded = jwtDecode(token);
        setUserRole(decoded.role); // 예: "USER" or "ADMIN"
      } catch (e) {
        console.error('❌ 토큰 디코딩 실패', e);
      }
    }
  }, []);

  const login = (token) => {
    localStorage.setItem('accessToken', token);
    setIsLogin(true);

    try {
      const decoded = jwtDecode(token);
      setUserRole(decoded.role);
    } catch (e) {
      console.error('❌ 토큰 디코딩 실패', e);
    }
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setIsLogin(false);
    setUserRole(null);
  };

  return (
    <AuthContext.Provider value={{ isLogin, userRole, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
