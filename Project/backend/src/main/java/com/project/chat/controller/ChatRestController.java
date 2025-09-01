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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.chat.dto.ChatDto;
import com.project.chat.dto.ChatRoomDto;
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
    public ResponseEntity<Page<ChatRoomDto>> getAllChatRooms(
            @PageableDefault(sort = "lastMessageTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ChatRoomDto> chatRoomsPage = chatService.findAllChatRoomsByPage(pageable);
        return ResponseEntity.ok(chatRoomsPage);
    }

    /**
     * 특정 채팅방의 모든 대화 기록을 조회합니다.
     */
    @GetMapping("/detail/{chatRoomNum}")
    public ResponseEntity<List<ChatDto>> getChatHistory(@PathVariable Long chatRoomNum, Principal principal) {
        List<ChatDto> chatHistory = chatService.getChatHistory(chatRoomNum);
        return ResponseEntity.ok(chatHistory);
    }

    
    /**
     * [회원용] 첫 채팅 시작 시 채팅방을 찾거나 새로 생성합니다.
     */
    @PostMapping("/detail")
    public ResponseEntity<ChatRoomEntity> findOrCreateChatRoomForMember(Principal principal) {
        String memberId = principal.getName();
        
        Optional<MemberEntity> optionalMember = memberService.findByMemberId(memberId);
        
        if (optionalMember.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        MemberEntity member = optionalMember.get();
        
        ChatRoomEntity chatRoom = chatService.findOrCreateChatRoom(member);
        
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * [관리자용] 특정 채팅방에 메시지를 전송합니다.
     */
    @PostMapping("/admin/send")
    public ResponseEntity<Void> sendAdminMessage(@RequestBody ChatDto chatDto, Principal principal) {
        String adminId = principal.getName();
        
        Optional<MemberEntity> optionalAdmin = memberService.findByMemberId(adminId);
        
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        MemberEntity adminMember = optionalAdmin.get();
        
        chatService.saveMessage(chatDto, adminMember);
        
        return ResponseEntity.ok().build();
    }
    
    
    /**
     * [관리자용] 특정 채팅방을 제거합니다.
     */
    @DeleteMapping("/admin/detail/{chatRoomNum}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable Long chatRoomNum) {
        chatService.deleteChatRoom(chatRoomNum);
        return ResponseEntity.noContent().build();
    }
}