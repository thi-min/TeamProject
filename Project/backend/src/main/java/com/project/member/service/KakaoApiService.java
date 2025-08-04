package com.project.member.service;

import com.project.member.dto.KakaoUserInfoDto;

public interface KakaoApiService {
	String getAccessToken(String code) throws Exception;
    KakaoUserInfoDto getUserInfo(String accessToken) throws Exception;
}
