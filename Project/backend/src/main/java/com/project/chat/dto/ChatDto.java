package com.project.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatDto {

    private Long chatRoomNum;
    private Long senderNum;
    private String senderRole;
    private String message;
}