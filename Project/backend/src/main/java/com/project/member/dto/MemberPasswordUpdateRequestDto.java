package com.project.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
//비밀번호 변경
// 1. 마이페이지 > 비밀번호변경 (memberNum 생략
// 2. 오래된 비밀번호 일경우(비밀번호가 만료된 상태라서 로그인x)
public class MemberPasswordUpdateRequestDto {
	public String memberId;
	
	@NotBlank(message = "현재 비밀번호를 입력해주세요.")
	private String currentPassword;    // 현재 비밀번호(일반 비밀번호 변경시 사용)
	
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,16}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
    )
    private String newPassword; //새 비밀번호
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
	private String newPasswordCheck;    // 새 비밀번호 확인
	
	private boolean isExpiredChange;   // 비밀번호 만료 변경인지 여부(요청구분용)

}