package com.project.chat.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.mapper.ChatMapper;
import com.project.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ChatService chatService;
    private final ChatMapper chatMapper;

    @MessageMapping("/chat.send")
    public void processMessage(ChatMessageRequestDto dto, SimpMessageHeaderAccessor headerAccessor) {
        Long roomId = dto.getChatRoomId();
        ChatRoomEntity room = null;
        if (roomId == null) {
            if (dto.getMemberNum() == null) return;
            room = chatService.createRoomForMember(dto.getMemberNum());
        } else {
            room = chatService.getRoom(roomId);
            if (room == null) {
                if (dto.getMemberNum() == null) return;
                room = chatService.createRoomForMember(dto.getMemberNum());
            }
        }
        ChatMessageEntity entity = chatMapper.toEntity(dto);
        entity.setChatRoom(room);
        chatService.saveMessage(entity);
        // broadcasting is done by service via messagingTemplate
    }
}