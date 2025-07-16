package com.project.member;

import java.time.LocalDate;

import com.project.common.MemberState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {

    private Long memberId;
    private String memberPw;
    private String memberName;
    private LocalDate memberBirth;
    private Integer memberSex;
    private String memberPhone;
    private String memberAddress;
    private Boolean memberLock;
    private MemberState memberState;
    private String kakaoId;
}