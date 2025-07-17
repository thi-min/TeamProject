package com.project.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//관리자가 사용자에게 메시지 전송할 때 사용
public class AdminChatSendRequestDto {
    private Long memberNum;    // 대상 사용자
    private String adminId;    // 관리자 아이디
    private String message;    // 답변 메시지
}
