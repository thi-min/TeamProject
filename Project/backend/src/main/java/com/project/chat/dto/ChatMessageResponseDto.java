package com.project.chat.dto;

import java.time.LocalDateTime;

import com.project.chat.entity.CheckState;

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
public class ChatMessageResponseDto {
    private Long chatMessageId; // 채팅메시지 번호
    private Long chatRoomId; // 채팅방 번호
    private Long memberNum; //회원 번호
    private String adminId; // 관리자 id 
    private LocalDateTime sendTime; // 보낸시간
    private String chatCont; // 대화 내용
    private CheckState chatCheck; // 채팅 확인상태
}