package com.project.chat.service;

import com.project.admin.entity.AdminEntity;
import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.dto.ChatRoomResponseDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.entity.CheckState;
import com.project.chat.repository.ChatMessageRepository;
import com.project.chat.repository.ChatRoomRepository;
import com.project.member.entity.MemberEntity;
import com.project.member.repository.MemberRepository;
import com.project.admin.repository.AdminRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    /**
     * 특정 회원의 채팅방을 조회하거나 새로 생성합니다.
     * 회원이 첫 채팅을 시도할 때 사용됩니다.
     * 관리자는 시스템 내 고정된 관리자를 사용한다고 가정합니다.
     * @param memberNum 회원 번호
     * @return ChatRoomResponseDto
     */
    @Transactional
    public ChatRoomResponseDto findOrCreateChatRoom(Long memberNum) {
        // 1. 해당 회원의 채팅방이 이미 존재하는지 확인
        return chatRoomRepository.findByMember_MemberNum(memberNum)
                .map(ChatRoomResponseDto::fromEntity) // 존재하면 DTO로 변환하여 반환
                .orElseGet(() -> {
                    // 2. 채팅방이 존재하지 않으면, 새로운 채팅방 생성
                    MemberEntity member = memberRepository.findById(memberNum)
                            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
                    
                    // 관리자는 시스템 내 유일한 한 명이라고 가정
                    AdminEntity admin = adminRepository.findById(1L) // 예시: ID가 1L인 관리자
                            .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

                    ChatRoomEntity newRoom = ChatRoomEntity.builder()
                            .member(member)
                            .admin(admin)
                            .createdDate(LocalDateTime.now())
                            .build();

                    ChatRoomEntity savedRoom = chatRoomRepository.save(newRoom);
                    return ChatRoomResponseDto.fromEntity(savedRoom);
                });
    }

    /**
     * 클라이언트로부터 받은 메시지를 데이터베이스에 저장합니다.
     * @param roomNum 채팅방 번호
     * @param requestDto 메시지 요청 DTO
     * @return 저장된 메시지 정보 DTO
     */
    @Transactional
    public ChatMessageResponseDto saveMessage(Long roomNum, ChatMessageRequestDto requestDto) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomNum)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 발신자 정보 설정 (회원 또는 관리자)
        MemberEntity memberSender = requestDto.isMemberSender() ?
                memberRepository.findById(requestDto.getSenderId())
                        .orElseThrow(() -> new IllegalArgumentException("발신자(회원)를 찾을 수 없습니다.")) : null;

        AdminEntity adminSender = !requestDto.isMemberSender() ?
                adminRepository.findById(requestDto.getSenderId())
                        .orElseThrow(() -> new IllegalArgumentException("발신자(관리자)를 찾을 수 없습니다.")) : null;

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .chatRoom(chatRoom)
                .memberSender(memberSender)
                .adminSender(adminSender)
                .messageContent(requestDto.getMessageContent())
                .sentDate(LocalDateTime.now())
                .checkState(CheckState.N) // 새 메시지는 항상 '미확인' 상태로 저장
                .build();

        ChatMessageEntity savedMessage = chatMessageRepository.save(chatMessage);
        return ChatMessageResponseDto.fromEntity(savedMessage);
    }

    /**
     * 특정 채팅방의 메시지 목록을 조회합니다.
     * @param roomNum 채팅방 번호
     * @return 최신순으로 정렬된 메시지 목록 DTO 리스트
     */
    public List<ChatMessageResponseDto> getChatMessages(Long roomNum) {
        List<ChatMessageEntity> messages = chatMessageRepository.findByChatRoom_RoomNumOrderBySentDateDesc(roomNum);
        return messages.stream()
                .map(ChatMessageResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 특정 채팅방의 모든 메시지 상태를 '확인됨(Y)'으로 업데이트합니다.
     * 일반적으로 사용자가 채팅방에 진입했을 때 호출됩니다.
     * @param roomNum 채팅방 번호
     */
    @Transactional
    public void markMessagesAsRead(Long roomNum) {
        List<ChatMessageEntity> unreadMessages = chatMessageRepository.findByChatRoom_RoomNumAndCheckState(roomNum, CheckState.N);
        unreadMessages.forEach(message -> message.setCheckState(CheckState.Y));
        chatMessageRepository.saveAll(unreadMessages);
    }
}