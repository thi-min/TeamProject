package com.project.chat.controller;

import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.repository.ChatRoomRepository;
import com.project.chat.service.ChatMessageService;
import com.project.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final ChatRoomRepository chatRoomRepository;

    // 모든 채팅룸(관리자용)
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomEntity>> listRooms() {
        return ResponseEntity.ok(chatRoomRepository.findAll());
    }

    // 특정 룸 조회
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomEntity> getRoom(@PathVariable Long roomId) {
        ChatRoomEntity r = chatRoomService.getRoom(roomId);
        if (r == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(r);
    }

    // 룸 생성 (회원번호로)
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomEntity> createRoom(@RequestParam Long memberNum) {
        ChatRoomEntity created = chatRoomService.createRoomForMember(memberNum);
        return ResponseEntity.ok(created);
    }

    // 룸의 메시지 목록
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageEntity>> getMessages(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatMessageService.getMessagesByRoom(roomId));
    }
}