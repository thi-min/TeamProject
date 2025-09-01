package com.project.chat.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDto {
    private Long chatRoomNum;
    private Long memberNum; // MemberEntity 대신 회원 번호만 내려줌
    private String memberName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}