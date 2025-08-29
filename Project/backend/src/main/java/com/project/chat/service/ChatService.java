package com.project.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chat.dto.ChatDto;
import com.project.chat.dto.ChatRoomDto;
import com.project.chat.entity.ChatEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.repository.ChatRepository;
import com.project.chat.repository.ChatRoomRepository;
import com.project.member.entity.MemberEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    
    /**
     * 특정 회원의 채팅방을 찾거나, 없으면 새로 생성합니다.
     * @param member 채팅방을 찾거나 생성할 회원
     * @return ChatRoomEntity
     */
    @Transactional
    public ChatRoomEntity findOrCreateChatRoom(MemberEntity member) {
        return chatRoomRepository.findByMember(member)
                .orElseGet(() -> {
                    ChatRoomEntity newChatRoom = new ChatRoomEntity();
                    newChatRoom.setMember(member);
                    newChatRoom.setLastMessage("채팅방이 생성되었습니다.");
                    newChatRoom.setLastMessageTime(LocalDateTime.now());
                    return chatRoomRepository.save(newChatRoom);
                });
    }

    /**
     * 새로운 채팅 메시지를 저장하고, 채팅방의 마지막 메시지 정보를 업데이트합니다.
     * @param chatDto 클라이언트로부터 받은 메시지 정보
     */
    @Transactional
    public void saveMessage(ChatDto chatDto) {
        Optional<ChatRoomEntity> optionalChatRoom = chatRoomRepository.findById(chatDto.getChatRoomNum());
        if (optionalChatRoom.isEmpty()) {
            throw new IllegalArgumentException("Chat room not found with ID: " + chatDto.getChatRoomNum());
        }
        
        ChatRoomEntity chatRoom = optionalChatRoom.get();

        ChatEntity chatMessage = new ChatEntity();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSenderNum(chatDto.getSenderNum());
        chatMessage.setSenderRole(chatDto.getSenderRole());
        chatMessage.setMessage(chatDto.getMessage());
        chatRepository.save(chatMessage);
        
        chatRoom.setLastMessage(chatDto.getMessage());
        chatRoom.setLastMessageTime(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
    }

    /**
     * [관리자용] 모든 채팅방 목록을 페이지네이션하여 조회합니다.
     * @param pageable 페이징 및 정렬 정보
     * @return ChatRoomDto의 페이지 객체
     */
    @Transactional(readOnly = true)
    public Page<ChatRoomDto> findAllChatRoomsByPage(Pageable pageable) {
        return chatRoomRepository.findAll(pageable)
                .map(room -> new ChatRoomDto(
                        room.getChatRoomNum(),
                        room.getMember().getMemberNum(),
                        room.getMember().getMemberName(),
                        room.getLastMessage(),
                        room.getLastMessageTime()
                ));
    }

    /**
     * 특정 채팅방의 모든 대화 기록을 조회합니다.
     * @param chatRoomNum 채팅방 번호
     * @return 해당 채팅방의 메시지 리스트 (ChatDto)
     */
    @Transactional(readOnly = true)
    public List<ChatDto> getChatHistory(Long chatRoomNum) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomNum)
                .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        return chatRepository.findByChatRoomOrderByTimestampAsc(chatRoom)
                .stream()
                .map(chat -> {
                    ChatDto dto = new ChatDto();
                    dto.setChatRoomNum(chat.getChatRoom().getChatRoomNum());
                    dto.setSenderNum(chat.getSenderNum());
                    dto.setSenderRole(chat.getSenderRole());
                    dto.setMessage(chat.getMessage());
                    dto.setTimestamp(chat.getTimestamp());
                    return dto;
                })
                .toList();
    }
    
    /**
     * [관리자용] 모든 채팅방 목록을 마지막 메시지 시간 순으로 조회합니다.
     * @return 마지막 메시지 시간 내림차순으로 정렬된 ChatRoomDto 리스트
     */
    @Transactional(readOnly = true)
    public List<ChatRoomDto> findAllChatRoomsOrderByTime() {
        return chatRoomRepository.findAllByOrderByLastMessageTimeDesc()
            .stream()
            .map(room -> new ChatRoomDto(
                room.getChatRoomNum(),
                room.getMember().getMemberNum(),
                room.getMember().getMemberName(),
                room.getLastMessage(),
                room.getLastMessageTime()
            ))
            .toList();
    }
    
    @Transactional
    public void deleteChatRoom(Long chatRoomNum) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomNum)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found with ID: " + chatRoomNum));
        
        chatRepository.deleteAllByChatRoom(chatRoom);
        chatRoomRepository.delete(chatRoom);
    }
}