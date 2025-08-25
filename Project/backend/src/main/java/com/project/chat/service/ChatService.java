package com.project.chat.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.admin.entity.AdminEntity;
import com.project.admin.repository.AdminRepository;
import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.dto.ChatRoomResponseDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.entity.CheckState;
import com.project.chat.repository.ChatMessageRepository;
import com.project.chat.repository.ChatRoomRepository;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 새로운 채팅방 생성(client)
    @Transactional
    public ChatRoomEntity createRoomForMember(Long memberNum) {
        ChatRoomEntity room = new ChatRoomEntity();
        room.setCreateAt(LocalDateTime.now());
        // 채팅방이 없을경우 예외 처리
        memberRepository.findByMemberNum(memberNum).ifPresent(room::setMember);
        return chatRoomRepository.save(room);
    }
    // 채팅방 정보 조회
    @Transactional(readOnly = true)
    public ChatRoomEntity getRoom(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }
    // 회원번호로 채팅방 조회
    public ChatRoomEntity getRoomByMemberId(Long memberNum) {
        return chatRoomRepository.findByMemberMemberNum(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("채팅방이 존재하지 않습니다."));
    }


    //채팅 메시지 저장
    @Transactional
    public ChatMessageEntity saveMessage(ChatMessageEntity msg) {
        if (msg.getSendTime() == null) msg.setSendTime(LocalDateTime.now());
        if (msg.getChatCheck() == null) msg.setChatCheck(com.project.chat.entity.CheckState.N);
        ChatMessageEntity saved = chatMessageRepository.save(msg);

        // 웹소켓으로 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/" + saved.getChatRoom().getChatRoomId(),
                toDto(saved));
        return saved;
    }
    // 특정 채팅방의 모든 메시지 정보 조회
    @Transactional(readOnly = true)
    public List<ChatMessageEntity> getMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomChatRoomIdOrderBySendTimeAsc(chatRoomId);
    }
    // 모든 채팅방 목록 조회 (목록 화면 전용)
    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getChatRoomList() {
        List<ChatRoomEntity> rooms = chatRoomRepository.findAll();
        return rooms.stream()
                .map(room -> {
                    // 마지막 메시지 조회
                    ChatMessageEntity lastMessage = chatMessageRepository
                            .findTopByChatRoomOrderBySendTimeDesc(room)
                            .orElse(null); // Optional 처리

                    // 미확인 메시지 존재 여부 확인
                    boolean hasNewMessage = chatMessageRepository.existsByChatRoomAndChatCheck(room, CheckState.N);

                    return ChatRoomResponseDto.builder()
                            .chatRoomId(room.getChatRoomId())
                            .memberName(room.getMember() != null ? room.getMember().getMemberName() : "알 수 없음")
                            .lastMessageContent(lastMessage != null ? lastMessage.getChatCont() : "채팅 내용이 없습니다.")
                            .lastMessageTime(lastMessage != null ? lastMessage.getSendTime() : room.getCreateAt())
                            .hasNewMessage(hasNewMessage)
                            .build();
                })
                .collect(Collectors.toList());

    }

    // Entity -> DTO 변환 메서드 ( 데이터 노출 최소화 )
    private ChatMessageResponseDto toDto(ChatMessageEntity entity) {
        if (entity == null) {
            return null;
        }
        ChatMessageResponseDto dto = new ChatMessageResponseDto();
        dto.setChatMessageId(entity.getChatMessageId());
        dto.setChatRoomId(entity.getChatRoom() != null ? entity.getChatRoom().getChatRoomId() : null);
        dto.setMemberNum(entity.getMember() != null ? entity.getMember().getMemberNum() : null);
        dto.setAdminId(entity.getAdmin() != null ? entity.getAdmin().getAdminId() : null);
        dto.setChatCont(entity.getChatCont());
        dto.setSendTime(entity.getSendTime());
        return dto;
    }
    // 채팅방 제거
    @Transactional
    public void deleteRoom(Long roomId) {
        // 채팅방 존재 확인
        ChatRoomEntity room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + roomId));

        // 채팅 메시지 삭제
        chatMessageRepository.deleteByChatRoom(room);

        // 채팅방 삭제
        chatRoomRepository.delete(room);
    }
 // Member 조회
    @Transactional(readOnly = true)
    public MemberEntity getMember(Long memberNum) {
        return memberRepository.findById(memberNum)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with num: " + memberNum));
    }

    // Admin 조회
    @Transactional(readOnly = true)
    public AdminEntity getAdmin(String adminId) {
        return adminRepository.findFirstByAdminId(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + adminId));
    }
    @Transactional(readOnly = true)
    public List<ChatRoomEntity> getAllRooms() {
        return chatRoomRepository.findAll();
    }
    @Transactional(readOnly = true)
    public boolean hasUnreadMessagesForMember(Long chatRoomId) {
        ChatRoomEntity room = chatRoomRepository.findById(chatRoomId)
            .orElseThrow(() -> new EntityNotFoundException("Chat room not found with id: " + chatRoomId));
        return chatMessageRepository.existsByChatRoomAndChatCheck(room, CheckState.N);
    }
}