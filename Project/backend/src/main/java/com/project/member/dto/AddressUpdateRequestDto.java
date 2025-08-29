package com.project.member.dto;

import lombok.*;

/** 내 주소 변경 요청 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressUpdateRequestDto {
    private String postcode;       // 우편번호(선택)
    private String roadAddress;    // 기본주소(필수 권장)
    private String detailAddress;  // 상세주소(선택)
}