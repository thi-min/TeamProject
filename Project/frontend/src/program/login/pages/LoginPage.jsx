//로그인페이지 UI + 로직

import React, { useState } from 'react';
import InputField from '../components/InputFieId';
import SubmitButton from '../components/SubmitButton';
import { loginUser } from '../services/auth';

const LoginPage = () => {
    //사용자 입력값 저장
    const [form, setForm] = useState({memberId:'',memberPw:''});
    //에러 메시지 저장
    const [error, setError] = useState('');
    //input 값이 변경될 때마다 form 상태 업데이트
    const handleChange = (e) => {
        setForm({...form, [e.target.name]: e.target.value});
    };

    //로그인 버튼 클릭시 실행되는 함수
    const heandleSubmit = async (e) => {
        e.preventDefault(); //폼 제출 기본 동작 방지
        try{
            const result = await loginUser(form);   //로그인 API호출
            console.log('로그인 성공', result);

            //JWT토큰 저장
            localStorage.setItem('accessToken', result.accessToken);

            //로그인 성공 후 페이지 이동
            window.location.href = '/main';
        }catch(err){
            //로그인 실패시 에러 메시지
            setError(err);
        }
    }

    //반환값
    return (
        <form className="loginForm" onSubmit={heandleSubmit}>
            <h3>로그인</h3>
            {/* 아이디 입력 */}
            <InputField label="아이디" name="memberId" value={form.memberId} onChange={handleChange} />
            {/* 비밀번호 입력 */}
            <InputField label="비밀번호" name="memberPw" value={form.memberPw} onChange={handleChange} />
            {/* 에러 메시지 출력 */}
            {error && <p style={{color:'red'}}>error</p>}
            {/* 로그인 버튼 */}
            <SubmitButton text="로그인" />
        </form>
    )
}
export default LoginPage;