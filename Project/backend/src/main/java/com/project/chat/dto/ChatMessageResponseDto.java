package com.project.chat.dto;

import com.project.common.enums.CheckState;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long chatMessageId;
    private Long chatRoomId;
    private Long memberNum;
    private String adminId;
    private LocalDateTime sendTime;
    private String chatCont;
    private CheckState chatCheck;
}