package com.project.chat.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.chat.entity.ChatEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.service.ChatService;
import com.project.member.entity.MemberEntity;
import com.project.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;
    private final MemberService memberService; // MemberService 주입

    /**
     * [관리자용] 모든 채팅방 목록을 페이지네이션하여 조회합니다.
     */
    @GetMapping("/admin/list")
    public ResponseEntity<Page<ChatRoomEntity>> getAllChatRooms(
            @PageableDefault(sort = "lastMessageTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ChatRoomEntity> chatRoomsPage = chatService.findAllChatRoomsByPage(pageable);
        return ResponseEntity.ok(chatRoomsPage);
    }

    /**
     * [관리자/회원용] 특정 채팅방의 모든 메시지 기록을 조회합니다.
     */
    @GetMapping("/history/{chatRoomNum}")
    public ResponseEntity<List<ChatEntity>> getChatHistory(@PathVariable Long chatRoomNum, Principal principal) {
        // TODO: Principal 객체를 사용하여 요청한 사용자가 채팅방에 접근할 권한이 있는지 확인
        List<ChatEntity> chatHistory = chatService.getChatHistory(chatRoomNum);
        return ResponseEntity.ok(chatHistory);
    }
    
    /**
     * [회원용] 첫 채팅 시작 시 채팅방을 찾거나 새로 생성합니다.
     */
    @PostMapping("/room")
    public ResponseEntity<ChatRoomEntity> findOrCreateChatRoomForMember(Principal principal) {
        // Principal 객체에서 회원 ID(이메일)를 가져옵니다.
        String memberId = principal.getName();
        
        // MemberService를 통해 회원 ID로 MemberEntity를 조회합니다.
        // Optional을 사용해 회원이 없을 경우를 안전하게 처리합니다.
        Optional<MemberEntity> optionalMember = memberService.findByMemberId(memberId);
        
        if (optionalMember.isEmpty()) {
            // 해당 ID의 회원을 찾지 못하면 404 에러를 반환합니다.
            // 이는 보통 JWT 토큰의 ID가 DB에 존재하지 않을 때 발생합니다.
            return ResponseEntity.notFound().build();
        }
        
        MemberEntity member = optionalMember.get();
        
        // 조회된 MemberEntity 객체를 서비스 계층으로 전달하여 채팅방을 찾거나 새로 생성합니다.
        ChatRoomEntity chatRoom = chatService.findOrCreateChatRoom(member);
        
        return ResponseEntity.ok(chatRoom);
    }
    
    /**
     * [관리자용] 특정 채팅방을 제거합니다.
     */
    @DeleteMapping("/admin/room/{chatRoomNum}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomNum) {
        chatService.deleteChatRoom(chatRoomNum);
        return ResponseEntity.noContent().build();
    }
}