// ChatDto.java (이미 있으신데 timestamp 추가 추천)
package com.project.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ChatDto {
    private Long chatRoomNum;
    private Long senderNum;
    private String senderRole;
    private String message;
    private LocalDateTime timestamp;
}
