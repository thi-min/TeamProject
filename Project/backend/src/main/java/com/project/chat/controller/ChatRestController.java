package com.project.chat.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.service.ChatService;
import com.project.member.entity.MemberEntity; // MemberEntity 임포트
import com.project.admin.entity.AdminEntity; // AdminEntity 임포트

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {
    private final ChatService chatService;

    // DTO를 엔티티로 변환하는 수동 메서드
    private ChatMessageEntity toEntity(ChatMessageRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ChatMessageEntity entity = new ChatMessageEntity();
        // DTO에서 MemberNum과 AdminId를 받아와서 Entity를 생성합니다.
        // 이 부분은 서비스 레이어에서 실제 MemberEntity와 AdminEntity를 조회하여 설정하는 것이 더 바람직합니다.
        // 현재는 예시를 위해 DTO의 값을 직접 설정합니다.
        
        // ChatMessageEntity에 MemberEntity와 AdminEntity 객체가 ManyToOne 관계이므로, 
        // 컨트롤러에서 직접 객체를 생성하거나 서비스에서 조회하여 설정해야 합니다.
        // 아래 코드는 임시로 MemberEntity와 AdminEntity를 생성하는 예시입니다.
        // 실제로는 MemberService, AdminService를 주입받아 조회해야 합니다.
        MemberEntity member = new MemberEntity();
        member.setMemberNum(dto.getMemberNum());
        
        AdminEntity admin = new AdminEntity();
        admin.setAdminId(dto.getAdminId());

        entity.setMember(member);
        entity.setAdmin(admin);
        entity.setChatCont(dto.getChatCont());
        entity.setSendTime(LocalDateTime.now());
        
        return entity;
    }

    // 엔티티를 DTO로 변환하는 수동 메서드
    private ChatMessageResponseDto toDto(ChatMessageEntity entity) {
        if (entity == null) {
            return null;
        }

        return ChatMessageResponseDto.builder()
                .chatMessageId(entity.getChatMessageId())
                .chatRoomId(entity.getChatRoom().getChatRoomId())
                .memberNum(entity.getMember() != null ? entity.getMember().getMemberNum() : null)
                .adminId(entity.getAdmin() != null ? entity.getAdmin().getAdminId() : null)
                .sendTime(entity.getSendTime())
                .chatCont(entity.getChatCont())
                .chatCheck(entity.getChatCheck())
                .build();
    }


    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomEntity>> listRooms() {
        return ResponseEntity.ok(chatService.getAllRooms());
    }

    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomEntity> createRoom(@RequestParam Long memberNum) {
        return ResponseEntity.ok(chatService.createRoomForMember(memberNum));
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(@PathVariable Long roomId) {
        List<ChatMessageEntity> list = chatService.getMessages(roomId);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponseDto> postMessage(@PathVariable Long roomId,
                                                              @RequestBody ChatMessageRequestDto req) {
        ChatMessageEntity entity = toEntity(req);
        ChatRoomEntity room = chatService.getRoom(roomId);
        if (room == null) return ResponseEntity.notFound().build();
        entity.setChatRoom(room);
        ChatMessageEntity saved = chatService.saveMessage(entity);
        return ResponseEntity.ok(toDto(saved));
    }
}
