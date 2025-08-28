package com.project.chat.controller;

import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * 클라이언트로부터 메시지를 받아 처리하고, 구독자들에게 메시지를 전송합니다.
     * @param roomNum 채팅방 번호
     * @param messageRequestDto 클라이언트가 보낸 메시지 DTO
     * @return 메시지 전송 후, 구독자에게 다시 보낼 메시지 DTO
     */
    @MessageMapping("/chat/{roomNum}") // 클라이언트가 메시지를 보낼 경로: /app/chat/{roomNum}
    @SendTo("/topic/chatRoom/{roomNum}") // 메시지 전송 후 해당 경로를 구독하는 클라이언트에게 반환
    public ChatMessageResponseDto sendMessage(
            @DestinationVariable Long roomNum,
            ChatMessageRequestDto messageRequestDto
    ) {
        // 1. 메시지 저장
        ChatMessageResponseDto savedMessage = chatService.saveMessage(roomNum, messageRequestDto);
        
        // 2. 메시지 확인 처리 (관리자 채팅방 진입 시)
        // 이 로직은 관리자가 채팅방에 들어왔을 때만 실행되어야 하지만, 간단한 예시로 여기에 포함합니다.
        // 실제로는 클라이언트의 특정 행동(팝업 오픈 등)에 따라 별도의 HTTP 요청으로 처리하는 것이 좋습니다.
        chatService.markMessagesAsRead(roomNum);
        
        return savedMessage;
    }
}