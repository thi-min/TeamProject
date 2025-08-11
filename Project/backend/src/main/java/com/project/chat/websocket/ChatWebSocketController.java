package com.project.chat.websocket;

import com.project.adopt.dto.ChatSocketAckDto;
import com.project.adopt.dto.ChatSocketPayload;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.service.ChatMessageService;
import com.project.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트 -> /app/chat.send 로 보냄
     * payload: ChatSocketPayload { chatRoomId, memberNum, adminId, content }
     */
    @MessageMapping("/chat.send")
    public void processMessage(ChatSocketPayload payload, SimpMessageHeaderAccessor headerAccessor,
                               @Header(name = "simpSessionId", required = false) String sessionId) {
        Long roomId = payload.getChatRoomId();
        // 룸이 없으면 생성(1:1의 경우 회원이 없으면 방 생성)
        ChatRoomEntity room;
        if (roomId == null) {
            if (payload.getMemberNum() != null) {
                room = chatRoomService.createRoomForMember(payload.getMemberNum());
            } else {
                // 예외처리: 룸이 없으며 member 정보도 없을 때는 무시하거나 에러 응답
                return;
            }
        } else {
            room = chatRoomService.getRoom(roomId);
            if (room == null) {
                // 룸이 없음: create or ignore
                if (payload.getMemberNum() != null) {
                    room = chatRoomService.createRoomForMember(payload.getMemberNum());
                } else {
                    return;
                }
            }
        }

        // 저장
        ChatMessageEntity saved = chatMessageService.saveMessage(room, payload.getMemberNum(), payload.getAdminId(), payload.getContent());

        // ACK DTO 생성 및 브로드캐스트
        ChatSocketAckDto ack = ChatSocketAckDto.builder()
                .chatMessageId(saved.getChatMessageId())
                .chatRoomId(room.getChatRoomId())
                .memberNum(payload.getMemberNum())
                .adminId(payload.getAdminId())
                .content(saved.getChatCont())
                .sendTime(saved.getSendTime())
                .delivered(true)
                .build();

        // 브로드캐스트: 구독자들이 listen하는 topic (예: /topic/chat/{roomId})
        messagingTemplate.convertAndSend("/topic/chat/" + room.getChatRoomId(), ack);
    }
}