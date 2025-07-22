package com.project.admin.dto;

import java.time.LocalDateTime;

import com.project.member.entity.MemberEntity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
//비밀번호 변경
public class AdminPasswordUpdateRequestDto {
	@NotBlank(message = "현재 비밀번호를 입력해주세요.")
	private String currentPassword;    // 현재 비밀번호(일반 비밀번호 변경시 사용)
	
    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,16}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
    )
    private String newPassword;	//새 비밀번호
    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
	private String newPasswordCheck;    //새 비밀번호 확인
}