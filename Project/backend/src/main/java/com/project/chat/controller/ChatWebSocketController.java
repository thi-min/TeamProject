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
import com.project.admin.entity.AdminEntity; // AdminEntity 임포트
import com.project.admin.service.AdminService; // AdminService 임포트

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final MemberService memberService;
    private final AdminService adminService; // AdminService 추가
    
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
        log.info("Principal Name: {}", principal != null ? principal.getName() : "null");
        
        if (principal == null) {
            log.error("Principal이 null입니다. 인증되지 않은 사용자입니다.");
            return;
        }
        
        try {
            // 발신자 역할 먼저 추출
            String senderRole = getSenderRoleFromPrincipal(principal);
            log.info("발신자 역할: {}", senderRole);
            
            // 역할에 따라 발신자 번호 추출
            Long senderNum = getSenderNumFromPrincipal(principal, senderRole);
            
            if (senderNum == null) {
                log.error("사용자 번호를 찾을 수 없습니다. Principal Name: {}", principal.getName());
                return;
            }
            
            log.info("발신자 번호: {}, 역할: {}", senderNum, senderRole);
            
            // DTO에 발신자 정보 설정
            chatDto.setSenderNum(senderNum);
            chatDto.setSenderRole(senderRole);
            
            // 메시지 저장
            chatService.saveMessage(chatDto);
            log.info("메시지 저장 완료");
            
            // 메시지 브로드캐스트
            String destination = "/sub/chat/room/" + chatDto.getChatRoomNum();
            messagingTemplate.convertAndSend(destination, chatDto);
            log.info("메시지 전송 완료 - 대상: {}", destination);
            
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
        
        log.info("=== 메시지 처리 완료 ===");
    }
    
    /**
     * Principal과 역할을 통해 사용자 번호를 추출합니다.
     * @param principal Spring Security Principal 객체
     * @param role 추출된 사용자 역할
     * @return 사용자 번호 (Member 또는 Admin의 기본키)
     */
    private Long getSenderNumFromPrincipal(Principal principal, String role) {
        try {
            String userId = principal.getName();
            log.debug("Principal에서 추출한 ID: {}, 역할: {}", userId, role);
            
            if ("MEMBER".equalsIgnoreCase(role)) {
                // Member 서비스 호출
                Optional<MemberEntity> memberOptional = memberService.findByMemberId(userId);
                if (memberOptional.isPresent()) {
                    return memberOptional.get().getMemberNum(); 
                }
            } else if ("ADMIN".equalsIgnoreCase(role)) {
                // Admin 서비스 호출
                Optional<AdminEntity> adminOptional = adminService.findByAdminId(userId);
                if (adminOptional.isPresent()) {
                    return adminOptional.get().getAdminNum(); // 관리자 번호를 가져오는 메서드
                }
            }
            
            log.warn("해당 ID와 역할에 맞는 사용자를 찾을 수 없습니다: ID={}, 역할={}", userId, role);
            return null;
            
        } catch (Exception e) {
            log.error("사용자 번호 추출 중 오류 발생: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Principal에서 사용자 역할을 추출합니다.
     * @param principal Spring Security Principal 객체
     * @return 사용자 역할 ("MEMBER", "ADMIN" 등)
     */
 // src/main/java/com/project/chat/controller/ChatWebSocketController.java

    private String getSenderRoleFromPrincipal(Principal principal) {
        try {
            if (principal instanceof Authentication) {
                Authentication authentication = (Authentication) principal;
                
                // ADMIN 역할이 있는지 먼저 확인
                boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                
                if (isAdmin) {
                    return "ADMIN";
                }
                
                // MEMBER 역할이 있는지 확인
                boolean isMember = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MEMBER"));
                    
                if (isMember) {
                    return "MEMBER";
                }
            }
            
            // 위에서 찾지 못하면 WARN 로그를 남기고 기본값(예: "MEMBER")을 반환
            log.warn("권한 정보를 찾을 수 없어 기본값 MEMBER를 반환합니다. Principal: {}", principal.getName());
            return "MEMBER";
            
        } catch (Exception e) {
            log.error("사용자 역할 추출 중 오류 발생: {}", e.getMessage(), e);
            return "MEMBER";
        }
    }
}
