package com.priject.admin.dto.request;

public class AdminMembverStateUpdateRequestDto{
  @NotNull(message = "변경할 회원 상태를 선택해주세요.")
  private MemberState memberState;
}
