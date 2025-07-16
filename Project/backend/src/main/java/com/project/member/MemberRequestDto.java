package com.project.member;

import com.project.entity.common.MemberState;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {

    private String memberId;
    private String memberPw;
    private String memberName;
    private String memberBirth;
    private Integer memberSex;
    private String memberPhone;
    private String memberAddress;
    private Boolean memberLock;
    private MemberState memberState;
    private String kakaoId;
}