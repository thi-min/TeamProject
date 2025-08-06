import { Link,useNavigate } from "react-router-dom";
import { useEffect } from 'react';

const LogoutLink = () => {
    const navigate = useNavigate();

    const handleLogout = (e) => {
        e.preventDefault(); //기본 링크 동작 방지
        //토큰 삭제
        localStorage.removeItem('accessToken');
        //메인 페이지로 이동
        navigate('/main');
    };
    return (<Link to="/logout" onClick={handleLogout}>로그아웃</Link>);

};

export default LogoutLink;