package com.project.member.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Data;

//회원가입 요청

@Data
public class MemberSignUpRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Pattern(regexp = "^[가-힣]{2,10}$", message = "이름은 한글 2~10자 이내로 입력하세요.")
    private String memberName;

    @NotBlank(message = "아이디는 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String memberId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    //(?=.*[A-Za-z]) 영문 1개 이상
    //(?=.*\\d) 숫자 1개 이상
    //(?=.*[!@#$%^&*()_+\\-={}:;"'<>?,./]) 특수문자 1개 이상
    //.{8,16} 전체길이 8~16
    @Pattern(
	    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}:;\"'<>?,./]).{8,16}$",
	    message = "비밀번호는 영문, 숫자, 특수문자를 포함한 8~16자리여야 합니다."
	)
	private String memberPw;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String memberPwCheck;

    @NotBlank(message = "생년월일은 필수입니다.")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "생년월일은 yyyy-MM-dd 형식으로 입력하세요.")
    private LocalDate memberBirth;
    //private String memberBirth;

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^\\d{10,11}$", message = "휴대폰 번호는 숫자만 10~11자리 입력하세요.")
    private String memberPhone;

    @NotBlank(message = "주소는 필수입니다.")
    private String memberAddress;
}
