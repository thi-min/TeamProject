package com.project.chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.dto.ChatRoomResponseDto;
import com.project.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatRestController {

    private final ChatService chatService;

    /**
     * 특정 회원의 채팅방을 조회하거나 새로 생성합니다.
     * @param memberNum 회원 번호
     * @return 채팅방 정보 DTO
     */
    @PostMapping("/room/{memberNum}")
    public ResponseEntity<ChatRoomResponseDto> findOrCreateChatRoom(@PathVariable Long memberNum) {
        ChatRoomResponseDto room = chatService.findOrCreateChatRoom(memberNum);
        return ResponseEntity.ok(room);
    }

    /**
     * 특정 채팅방의 메시지 목록을 조회합니다.
     * @param roomNum 채팅방 번호
     * @return 메시지 목록 DTO 리스트
     */
    @GetMapping("/messages/{roomNum}")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatMessages(@PathVariable Long roomNum) {
        List<ChatMessageResponseDto> messages = chatService.getChatMessages(roomNum);
        return ResponseEntity.ok(messages);
    }
}