package com.project.chat.dto;

import com.project.common.enums.CheckState;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequestDto {
    private Long chatRoomId;
    private Long memberNum;
    private String adminId;
    private String chatCont;
}