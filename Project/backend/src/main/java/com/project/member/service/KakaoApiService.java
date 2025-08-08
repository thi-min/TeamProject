package com.project.member.service;

import com.project.member.dto.KakaoSignUpRequestDto;
import com.project.member.dto.KakaoUserInfoDto;
import com.project.member.entity.MemberEntity;

public interface KakaoApiService {

    // 🔑 인가코드를 통해 access token 발급
    String getAccessToken(String code) throws Exception;

    // 🧑 사용자 정보 요청
    KakaoUserInfoDto getUserInfo(String accessToken) throws Exception;

}
