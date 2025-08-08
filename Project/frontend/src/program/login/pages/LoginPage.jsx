//로그인페이지 UI + 로직
// src/pages/LoginPage.jsx

import React, { useState } from 'react';
import InputFieId from '../components/InputFieId';
import SubmitButton from '../components/SubmitButton';
import { loginUser } from '../services/auth';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../common/context/AuthContext'; //전역 로그인 상태 불러오기

console.log('[LoginPage] sees AuthContext id =', window.__AUTH_CTX_ID__);

const LoginPage = () => {
    const [form, setForm] = useState({ memberId: '', memberPw: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const result = await loginUser(form); // 로그인 API 호출
            console.log('✅ 로그인 성공:', result);
            
            //const token =result.accessToken;

            login({
                accessToken: result.accessToken,
                refreshToken: result.refreshToken // 백엔드 응답에 있으면 같이 전달
            });
            // JWT 토큰 저장
            //localStorage.setItem('accessToken', result.accessToken);

            alert("로그인 성공");

            // 메인 페이지 이동 (SPA 방식)
            navigate('/');
        } catch (err) {
            // 에러 메시지 처리
            alert("로그인 실패");
            console.error('❌ 로그인 실패:', err);
            setError('로그인 실패: 아이디 또는 비밀번호를 확인하세요.');
        }
    };

    return (
        <form className="loginForm" onSubmit={handleSubmit}>
            <h3>로그인</h3>

            <InputFieId
                label="아이디"
                name="memberId"
                value={form.memberId}
                onChange={handleChange}
                type="text"
            />

            <InputFieId
                label="비밀번호"
                name="memberPw"
                value={form.memberPw}
                onChange={handleChange}
                type="password"
            />

            {error && <p style={{ color: 'red' }}>{error}</p>}

            <SubmitButton text="로그인" />
        </form>
    );
};

export default LoginPage;
