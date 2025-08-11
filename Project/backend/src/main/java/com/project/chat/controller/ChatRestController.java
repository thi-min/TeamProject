package com.project.chat.controller;

import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.mapper.ChatMapper;
import com.project.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;
    private final ChatMapper chatMapper;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomEntity>> listRooms() {
        // admin view
        return ResponseEntity.ok(chatService.getAllRooms());
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomEntity> createRoom(@RequestParam Long memberNum) {
        return ResponseEntity.ok(chatService.createRoomForMember(memberNum));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(@PathVariable Long roomId) {
        List<ChatMessageEntity> list = chatService.getMessages(roomId);
        return ResponseEntity.ok(list.stream().map(chatMapper::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponseDto> postMessage(@PathVariable Long roomId,
                                                              @RequestBody ChatMessageRequestDto req) {
        ChatMessageEntity entity = chatMapper.toEntity(req);
        // ensure room entity is set
        ChatRoomEntity room = chatService.getRoom(roomId);
        if (room == null) return ResponseEntity.notFound().build();
        entity.setChatRoom(room);
        ChatMessageEntity saved = chatService.saveMessage(entity);
        return ResponseEntity.ok(chatMapper.toDto(saved));
    }
}