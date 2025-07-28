package com.project.admin.dto;

import com.project.member.entity.MemberState;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class AdminMemberStateUpdateResponseDto{
  private Long memberNum;
  private MemberState memberState;
  private String message;
}
