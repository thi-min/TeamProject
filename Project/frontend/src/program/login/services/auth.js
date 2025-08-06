//로그인관련 API

import axios from 'axios';

//로그인 요청 백엔드로 전송
//param : {object} loginData - 로그인 폼 데이터(memberId, memberPw)
//return : {Promise<Object>} - 로그인 성공시 JWT토큰 등 응답 데이터
export const loginUser = async ({memberId, memberPw}) => {
    try{
        const response = await axios.post('/auth/login', { memberId, memberPw });
        return response.data;
    }catch(errer){
        //로그인 실패시 메시지 출력
        throw errer.response?.data?.message || '로그인 실패';
    }
};