package com.project.chat.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.project.chat.dto.ChatDto;
import com.project.chat.dto.ChatWebSocketDto;
import com.project.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 클라이언트가 /pub/topic/general로 메시지를 보낼 때 호출됩니다.
     * @param chatDto 클라이언트로부터 받은 메시지 (내용, 발신자 번호 등)
     * @param principal 현재 웹소켓에 연결된 사용자 정보 (Spring Security의 Principal)
     */
    @MessageMapping("/topic/general")
    public void sendMessage(ChatWebSocketDto chatDto, Principal principal) {
        log.info("Received message: {}", chatDto.getMessage());
        log.info("Principal Name: {}", principal.getName());

        Long senderNum = getSenderNumFromPrincipal(principal);
        String senderRole = getSenderRoleFromPrincipal(principal);

        ChatDto chatForService = new ChatDto();
        chatForService.setChatRoomNum(chatDto.getChatRoomNum());
        chatForService.setSenderNum(senderNum);
        chatForService.setSenderRole(senderRole);
        chatForService.setMessage(chatDto.getMessage());
        
        chatService.saveMessage(chatForService);

        // ⚠️ 수정된 부분: 메시지 수신 채널을 동적으로 설정합니다.
        String destination = "/sub/chat/room/" + chatDto.getChatRoomNum();
        messagingTemplate.convertAndSend(destination, chatForService);
    }
    
    private Long getSenderNumFromPrincipal(Principal principal) {
        // 실제 구현에서는 principal.getName()으로 얻은 ID를 기반으로 DB에서 사용자 번호를 조회합니다.
        // 예를 들어, memberService.findByMemberId(principal.getName()).getMemberNum();
        return 1L; // 임시 값
    }

    private String getSenderRoleFromPrincipal(Principal principal) {
        // 실제 구현에서는 Principal 객체의 권한(Authentication.getAuthorities())을 사용합니다.
        // 예를 들어, ((Authentication) principal).getAuthorities()...
        return "MEMBER"; // 임시 값
    }
}