package com.priject.admin.dto.response;

import com.priject.member.entity.enumtype.MemberState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class AdminMemberStateUpdateReponseDto{
  private Long memberNum;
  private MemberState memberState;
  pricvate String message;
}
