package com.project.alarm;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
//사용자 목록과 마지막 채팅 내용을 리스트로 보여줄 때
public class ChatUserSummaryDto {
    private Long memberNum;				// 회원 번호
    private String memberName;          // 사용자 이름
    private String lastMessage;         // 마지막 채팅 내용
    private LocalDateTime lastSendTime; // 마지막 채팅 시각
    private Boolean isRead;             // 읽음 여부
}