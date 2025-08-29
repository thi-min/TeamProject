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
        log.info("Principal Name: {}", principal.getName()); // 인증된 사용자의 ID를 확인할 수 있습니다.

        // 1. Principal 객체를 활용하여 발신자 정보(번호, 역할)를 가져옵니다.
        // Spring Security와 웹소켓 인터셉터가 이 정보를 이미 principal에 넣어주었습니다.
        Long senderNum = getSenderNumFromPrincipal(principal);
        String senderRole = getSenderRoleFromPrincipal(principal);

        // 2. ChatDto로 변환하여 서비스 계층으로 전달합니다.
        ChatDto chatForService = new ChatDto();
        chatForService.setChatRoomNum(chatDto.getChatRoomNum());
        chatForService.setSenderNum(senderNum);
        chatForService.setSenderRole(senderRole);
        chatForService.setMessage(chatDto.getMessage());
        
        // 3. 서비스 계층을 통해 메시지를 DB에 저장합니다.
        chatService.saveMessage(chatForService);

        // 4. 메시지 브로커를 통해 구독자에게 메시지를 전송합니다.
        messagingTemplate.convertAndSend("/sub/queue/test", chatForService);
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