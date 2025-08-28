package com.project.member.dto;

import lombok.Data;

@Data
public class KakaoUserInfoDto {
    private String kakaoId;      // ✔ 카카오 고유 ID → memberId로 사용
    private String email;        // ✔ 이메일 (카카오 계정)
    private String name;     // ✔ 이름으로 사용
    private String gender;       // ✔ "male" 또는 "female"
    private String birthday;     // ✔ "MMDD" (ex. "0214")
    private String birthyear;    // ✔ "YYYY" (ex. "1995")
    private String phoneNumber;  // ✔ "+82 10-1234-5678"
}
