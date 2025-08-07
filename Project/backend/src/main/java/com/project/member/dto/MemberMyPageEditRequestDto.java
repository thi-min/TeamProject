package com.project.member.dto;

import java.time.LocalDate;

import com.project.member.entity.MemberSex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MemberMyPageEditRequestDto {
	private String memberName;       // 수정 불가 - 받아도 무시
	private String memberPhone;      // 수정 가능
    private String memberAddress;    // 수정 가능
    private boolean smsAgree;        // ✔ 포함됨

}
