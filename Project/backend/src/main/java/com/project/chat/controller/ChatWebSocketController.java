package com.project.chat.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;

import com.project.chat.dto.ChatDto;
import com.project.chat.service.ChatService;
import com.project.member.entity.MemberEntity;
import com.project.member.service.MemberService;
import com.project.admin.entity.AdminEntity;
import com.project.admin.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final MemberService memberService;
    private final AdminService adminService;
    
    /**
     * 클라이언트가 /pub/chat/message로 메시지를 보낼 때 호출됩니다.
     * @param chatDto 클라이언트로부터 받은 메시지 페이로드
     * @param principal 현재 웹소켓에 연결된 사용자 정보 (Spring Security의 Principal)
     */
    @MessageMapping("/chat/message")
    public void sendMessage(ChatDto chatDto, Principal principal) {
        log.info("=== 메시지 수신 시작 ===");
        log.info("받은 메시지: {}", chatDto.getMessage());
        log.info("채팅방 번호: {}", chatDto.getChatRoomNum());
        
        if (principal == null) {
            log.error("Principal이 null입니다. 인증되지 않은 사용자입니다.");
            return;
        }
        
        
        try {
            String userId = principal.getName();
            String senderRole = "MEMBER"; // 기본값
            Object senderEntity = null;

            if (principal instanceof Authentication) {
                Authentication authentication = (Authentication) principal;
                Optional<? extends GrantedAuthority> authorityOptional = authentication.getAuthorities().stream().findFirst();
                if (authorityOptional.isPresent()) {
                    String role = authorityOptional.get().getAuthority().replace("ROLE_", "");
                    senderRole = role;
                }
            } else {
                log.warn("Principal이 Authentication 타입이 아닙니다. ID: {}", userId);
            }

            if ("MEMBER".equalsIgnoreCase(senderRole)|| "USER".equalsIgnoreCase(senderRole)) {
                Optional<MemberEntity> memberOptional = memberService.findByMemberId(userId);
                if (memberOptional.isPresent()) {
                    senderEntity = memberOptional.get();
                }
            } else if ("ADMIN".equalsIgnoreCase(senderRole)) {
                Optional<AdminEntity> adminOptional = adminService.findByAdminId(userId);
                if (adminOptional.isPresent()) {
                    senderEntity = adminOptional.get();
                }
            
	        } else if ("USER".equalsIgnoreCase(senderRole)) { // 이 부분을 추가합니다.
	            Optional<MemberEntity> memberOptional = memberService.findByMemberId(userId);
	            if (memberOptional.isPresent()) {
	                senderEntity = memberOptional.get();
	            }
        }
            
            
            if (senderEntity == null) {
                log.error("발신자 정보를 찾을 수 없습니다. ID: {}, 역할: {}", userId, senderRole);
                return;
            }
            
            // DTO에 발신자 정보 설정
            chatDto.setSenderRole(senderRole);
            if ("MEMBER".equalsIgnoreCase(senderRole)) {
                chatDto.setSenderNum(((MemberEntity) senderEntity).getMemberNum());
            } else if ("ADMIN".equalsIgnoreCase(senderRole)) {
                chatDto.setSenderNum(((AdminEntity) senderEntity).getAdminNum());
            }
            
            // 통합된 saveMessage 메서드 호출
            chatService.saveMessage(chatDto, senderEntity);
            
            log.info("메시지 저장 완료");
            
            // 메시지 브로드캐스트
            String destination = "/sub/chat/detail/" + chatDto.getChatRoomNum();
            messagingTemplate.convertAndSend(destination, chatDto);
            log.info("메시지 전송 완료 - 대상: {}", destination);
            
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("=== 메시지 처리 완료 ===");
    }
    
    // 이 메서드는 더 이상 필요하지 않습니다.
    // private String getSenderRoleFromPrincipal(Principal principal) { ... }
}
