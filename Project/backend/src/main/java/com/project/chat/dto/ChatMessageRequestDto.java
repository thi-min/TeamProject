package com.project.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDto {
    private Long chatRoomId; // 채팅방 id
    private Long memberNum; // 회원 번호
    private String adminId; // 관리자 id
    private String chatCont; // 대화 내용 - service에서 chatmessageentity와 연결
}