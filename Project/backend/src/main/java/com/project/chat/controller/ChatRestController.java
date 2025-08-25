package com.project.chat.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.admin.dto.AdminMemberDetailResponseDto;
import com.project.admin.entity.AdminEntity;
import com.project.admin.service.AdminService;
import com.project.animal.entity.AnimalEntity;
import com.project.animal.service.AnimalService;
import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.dto.ChatRoomResponseDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.service.ChatService;
import com.project.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final AdminService adminService; // AdminService 주입
    private final AnimalService animalService; // AnimalService 주입

    // DTO를 엔티티로 변환
    private ChatMessageEntity toEntity(ChatMessageRequestDto dto) {
        if (dto == null) return null;

        ChatMessageEntity entity = new ChatMessageEntity();

        // MemberEntity 조회 via AdminService
        if (dto.getMemberNum() != null) {
            AdminMemberDetailResponseDto detail = adminService.adminMemberDetailView(dto.getMemberNum());
            
            // DTO → Entity 변환
            MemberEntity member = MemberEntity.builder()
                    .memberNum(detail.getMemberNum())
                    .memberName(detail.getMemberName())
                    // 필요한 필드 추가
                    .build();

            entity.setMember(member);
        }

        // AdminEntity 조회
        if (dto.getAdminId() != null) {
            AdminEntity admin = chatService.getAdmin(dto.getAdminId());
            entity.setAdmin(admin);
        }

        entity.setChatCont(dto.getChatCont());
        entity.setSendTime(LocalDateTime.now());

        return entity;
    }

    // 엔티티를 DTO로 변환
    private ChatMessageResponseDto toDto(ChatMessageEntity entity) {
        if (entity == null) return null;

        return ChatMessageResponseDto.builder()
                .chatMessageId(entity.getChatMessageId())
                .chatRoomId(entity.getChatRoom() != null ? entity.getChatRoom().getChatRoomId() : null)
                .memberNum(entity.getMember() != null ? entity.getMember().getMemberNum() : null)
                .adminId(entity.getAdmin() != null ? entity.getAdmin().getAdminId() : null)
                .sendTime(entity.getSendTime())
                .chatCont(entity.getChatCont())
                .chatCheck(entity.getChatCheck())
                .build();
    }

    // 채팅방 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomEntity>> listRooms() {
        return ResponseEntity.ok(chatService.getAllRooms());
    }

    // 채팅방 생성
    @PostMapping("/create-room")
    public ResponseEntity<ChatRoomResponseDto> createRoom(@RequestParam Long memberNum) {
        ChatRoomEntity room = chatService.createRoomForMember(memberNum);
        ChatRoomResponseDto dto = ChatRoomResponseDto.builder()
                .chatRoomId(room.getChatRoomId())
                .build();
        return ResponseEntity.ok(dto);
    }

    // 회원번호로 채팅방 조회
    @GetMapping("/list/by-member/{memberNum}")
    public ResponseEntity<ChatRoomResponseDto> getRoomByMemberId(@PathVariable Long memberNum) {
        ChatRoomEntity room = chatService.getRoomByMemberId(memberNum);
        if (room == null) return ResponseEntity.notFound().build();

        ChatRoomResponseDto dto = ChatRoomResponseDto.builder()
                .chatRoomId(room.getChatRoomId())
                .build();
        return ResponseEntity.ok(dto);
    }

    // 채팅 상태 조회
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getChatStatus(@RequestParam Long memberNum) {
        ChatRoomEntity room = chatService.getRoomByMemberId(memberNum);
        Map<String, Object> response = new HashMap<>();
        if (room != null) {
            boolean hasUnreadMessages = chatService.hasUnreadMessagesForMember(room.getChatRoomId());
            response.put("chatRoomId", room.getChatRoomId());
            response.put("hasUnreadMessages", hasUnreadMessages);
        } else {
            response.put("chatRoomId", null);
            response.put("hasUnreadMessages", false);
        }
        return ResponseEntity.ok(response);
    }

    // 메시지 목록 조회
    @GetMapping("/list/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(@PathVariable Long roomId) {
        List<ChatMessageEntity> list = chatService.getMessages(roomId);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    // 메시지 전송
    @PostMapping("/room/{roomId}/messages")
    public ResponseEntity<ChatMessageResponseDto> postMessage(@PathVariable Long roomId,
                                                              @RequestBody ChatMessageRequestDto req) {
        ChatMessageEntity entity = toEntity(req);
        ChatRoomEntity room = chatService.getRoom(roomId);
        if (room == null) return ResponseEntity.notFound().build();
        entity.setChatRoom(room);
        ChatMessageEntity saved = chatService.saveMessage(entity);
        return ResponseEntity.ok(toDto(saved));
    }

    // 채팅방 삭제
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        chatService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    // 동물 입양 상담 채팅 시작
    @PostMapping("/start-adoption-chat")
    public ResponseEntity<ChatRoomResponseDto> startAdoptionChat(@RequestParam Long memberNum,
                                                                 @RequestParam Long animalId) {
        ChatRoomEntity room = chatService.getRoomByMemberId(memberNum);
        if (room == null) {
            room = chatService.createRoomForMember(memberNum);
        }

        AnimalEntity animal = animalService.getAnimal(animalId);
        String chatCont = "안녕하세요, " + animal.getAnimalName() + " 입양 상담 문의드립니다.";

        ChatMessageEntity firstMessage = new ChatMessageEntity();
        firstMessage.setChatRoom(room);
        // MemberEntity 설정 via AdminService
        AdminMemberDetailResponseDto detail = adminService.adminMemberDetailView(memberNum);
        MemberEntity member = MemberEntity.builder()
                .memberNum(detail.getMemberNum())
                .memberName(detail.getMemberName())
                .build();
        firstMessage.setMember(member);

        firstMessage.setChatCont(chatCont);
        chatService.saveMessage(firstMessage);

        ChatRoomResponseDto dto = ChatRoomResponseDto.builder()
                .chatRoomId(room.getChatRoomId())
                .build();

        return ResponseEntity.ok(dto);
    }
}

