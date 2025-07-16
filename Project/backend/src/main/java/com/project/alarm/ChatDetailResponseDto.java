package com.project.alarm;

import java.time.LocalDateTime;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
//특정 사용자를 클릭했을 때 해당 채팅 내역 리스트
public class ChatDetailResponseDto {
    private String sender;              // "ADMIN" 또는 "USER"
    private String message;             // 채팅 내용
    private LocalDateTime sendTime;     // 보낸 시각
    private String chatCheck;           // "Y" / "N"
}
