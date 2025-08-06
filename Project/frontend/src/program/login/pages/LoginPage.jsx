//로그인페이지 UI + 로직
// src/pages/LoginPage.jsx

import React, { useState } from 'react';
import InputFieId from '../components/InputFieId';
import SubmitButton from '../components/SubmitButton';
import { loginUser } from '../services/auth';
import { useNavigate } from 'react-router-dom';

const LoginPage = () => {
    const [form, setForm] = useState({ memberId: '', memberPw: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const result = await loginUser(form); // 로그인 API 호출
            console.log('✅ 로그인 성공:', result);

            // JWT 토큰 저장
            localStorage.setItem('accessToken', result.accessToken);

            // 메인 페이지 이동 (SPA 방식)
            navigate('/main');
        } catch (err) {
            // 에러 메시지 처리
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
