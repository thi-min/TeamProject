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

        // 채팅방 ID가 없으면 회원 ID로 채팅방을 생성 (입양 문의 시 최초 메시지)
        if (roomId == null && dto.getMemberNum() != null) {
            room = chatService.createRoomForMember(dto.getMemberNum());
        } else {
            // 채팅방 ID가 있으면 기존 채팅방을 조회
            room = chatService.getRoom(roomId);
            if (room == null) {
                // 채팅방이 존재하지 않으면 오류 처리 또는 새로 생성
                // 여기서는 새로운 채팅방을 생성하는 로직으로 구성
                if (dto.getMemberNum() == null) return;
                room = chatService.createRoomForMember(dto.getMemberNum());
            }
        }
        // 메시지 엔티티로 변환 및 저장
        ChatMessageEntity entity = toEntity(dto, room, headerAccessor);
        chatService.saveMessage(entity);
        // 브로드캐스팅은 서비스 레이어에서 처리
    }

    // DTO -> Entity 변환 메서드
     private ChatMessageEntity toEntity(ChatMessageRequestDto dto, ChatRoomEntity room, SimpMessageHeaderAccessor headerAccessor) {
        if (dto == null || room == null) {
            return null;
        }

        ChatMessageEntity entity = new ChatMessageEntity();
        
        // WebSocket 세션에서 사용자 정보(MemberNum 또는 AdminId) 추출
        String principalType = (String) headerAccessor.getSessionAttributes().get("principalType");
        String principalValue = (String) headerAccessor.getSessionAttributes().get("principalValue");

        if ("MEMBER".equals(principalType)) {
            MemberEntity member = chatService.getMember(Long.parseLong(principalValue));
            entity.setMember(member);
            entity.setAdmin(null);
        } else if ("ADMIN".equals(principalType)) {
            AdminEntity admin = chatService.getAdmin(principalValue);
            entity.setAdmin(admin);
            entity.setMember(null);
        }

        entity.setChatCont(dto.getChatCont());
        entity.setChatCheck(CheckState.N);
        entity.setSendTime(LocalDateTime.now());
        entity.setChatRoom(room);

        return entity;
    }
}