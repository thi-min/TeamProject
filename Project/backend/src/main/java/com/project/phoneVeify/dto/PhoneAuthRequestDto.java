package com.project.phoneVeify.dto;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.phoneVeify.entity.PhoneAuthEntity;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

//사용자 휴대폰 번호 입력
//중복된 사용자가 있는지 조회.
public class PhoneAuthRequestDto{
    @Pattern(regexp = "^010\\d{8}$", message = "올바른 휴대폰 번호 형식이어야 합니다.")
    private String phoneNum;
    
}
