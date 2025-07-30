package com.project.admin.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//관리자 로그인
public class AdminLoginResponseDto {
	private String adminNum; //관리자번호
    private String adminId; //아이디
    private String adminEmail; //이메일
    private String adminPhone; //핸드폰 번호
    private LocalDateTime connectData; //접속일시
    //private String authority; //프론트에서 사용할 권한체크(사용자, 관리자)
    private String accessToken; // JWT 또는 세션 기반이라면 포함
    private String refreshToken;
    private String message; //메시지
}