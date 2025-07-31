// 📁 com.project.member.dto.FindPasswordRequestDto.java
package com.project.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberFindPasswordRequestDto {
    private String memberId;
    private String memberName;
    private String memberPhone;
}
