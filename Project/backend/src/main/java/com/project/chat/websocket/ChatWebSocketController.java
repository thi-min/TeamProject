package com.project.chat.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.entity.CheckState;
import com.project.chat.service.ChatService;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ChatService chatService;

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
        ChatMessageEntity entity = toEntity(dto, room);
        chatService.saveMessage(entity);
        // broadcasting is done by service via messagingTemplate
    }

    // DTO -> Entity 변환 메서드
    private ChatMessageEntity toEntity(ChatMessageRequestDto dto, ChatRoomEntity room) {
        if (dto == null) {
            return null;
        }
        ChatMessageEntity entity = new ChatMessageEntity();
        
        // 필드명 불일치 수정
        entity.setChatCont(dto.getChatCont());
        entity.setChatCheck(CheckState.N);
        entity.setSendTime(LocalDateTime.now());
        entity.setChatRoom(room);
        
        // ChatMessageRequestDto에 없는 필드는 변환 로직에서 제거
        // entity.setMessageId(dto.getChatMessageId());
        // entity.setAlarmTime(dto.getAlarmTime());

        // Assuming member is also needed, and can be retrieved from service or DTO.
        // This part would need more context on how to get the MemberEntity.
        // For now, it is commented out.
        // if (dto.getMemberNum() != null) {
        //     MemberEntity member = chatService.getMember(dto.getMemberNum());
        //     entity.setMember(member);
        // }
        
        return entity;
    }
}