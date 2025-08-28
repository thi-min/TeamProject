package com.project.chat.dto;

import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.CheckState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long messageNum;
    private Long senderId;
    private boolean isMemberSender; // true: 회원, false: 관리자
    private String messageContent;
    private LocalDateTime sentDate;
    private CheckState checkState;

    public static ChatMessageResponseDto fromEntity(ChatMessageEntity chatMessage) {
        return ChatMessageResponseDto.builder()
                .messageNum(chatMessage.getMessageNum())
                .senderId(chatMessage.getSenderId())
                .isMemberSender(chatMessage.isMemberSender())
                .messageContent(chatMessage.getMessageContent())
                .sentDate(chatMessage.getSentDate())
                .checkState(chatMessage.getCheckState())
                .build();
    }
}