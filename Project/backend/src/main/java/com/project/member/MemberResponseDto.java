package com.project.member;

import com.project.entity.common.MemberState;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {

    private Long memberNum;
    private String memberId;
    private String memberName;
    private String memberPhone;
    private String memberAddress;
    private LocalDate memberDay;
    private MemberState memberState;
}