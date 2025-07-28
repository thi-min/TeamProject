package com.project.admin.dto;

import com.project.member.entity.MemberState;
import jakarta.validation.constraints.NotNull;

public class AdminMemberStateUpdateRequestDto{
  @NotNull(message = "변경할 회원 상태를 선택해주세요.")
  private MemberState memberState;
}
