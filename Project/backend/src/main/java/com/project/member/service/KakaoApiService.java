package com.project.member.service;

import org.springframework.stereotype.Service;

import com.project.member.dto.KakaoUserInfoDto;

@Service
public interface KakaoApiService {
	String getAccessToken(String code) throws Exception;
    KakaoUserInfoDto getUserInfo(String accessToken) throws Exception;
}
