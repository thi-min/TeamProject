package com.project.chat.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.chat.dto.ChatDto;
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
    
    // MemberEntity와 AdminEntity는 예시이므로, 실제 프로젝트 구조에 맞게 수정해야 합니다.
    // private final MemberRepository memberRepository;
    // private final AdminRepository adminRepository;

    /**
     * 특정 회원의 채팅방을 찾거나, 없으면 새로 생성합니다.
     * @param member 채팅방을 찾거나 생성할 회원
     * @return ChatRoomEntity
     */
    @Transactional
    public ChatRoomEntity findOrCreateChatRoom(MemberEntity member) {
        return chatRoomRepository.findByMember(member)
                .orElseGet(() -> {
                    // 채팅방이 없을 경우 새로운 채팅방을 생성합니다.
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
        // 채팅방 찾기
        Optional<ChatRoomEntity> optionalChatRoom = chatRoomRepository.findById(chatDto.getChatRoomNum());
        if (optionalChatRoom.isEmpty()) {
            // 해당 채팅방이 존재하지 않으면 예외 처리
            throw new IllegalArgumentException("Chat room not found with ID: " + chatDto.getChatRoomNum());
        }
        
        ChatRoomEntity chatRoom = optionalChatRoom.get();

        // ChatEntity 생성 및 저장
        ChatEntity chatMessage = new ChatEntity();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSenderNum(chatDto.getSenderNum());
        chatMessage.setSenderRole(chatDto.getSenderRole());
        chatMessage.setMessage(chatDto.getMessage());
        chatRepository.save(chatMessage);
        
        // 채팅방의 마지막 메시지 정보 업데이트
        chatRoom.setLastMessage(chatDto.getMessage());
        chatRoom.setLastMessageTime(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
    }

    /**
     * 특정 채팅방의 모든 대화 기록을 조회합니다.
     * @param chatRoomNum 채팅방 번호
     * @return 해당 채팅방의 메시지 리스트
     */
    @Transactional(readOnly = true)
    public List<ChatEntity> getChatHistory(Long chatRoomNum) {
        Optional<ChatRoomEntity> optionalChatRoom = chatRoomRepository.findById(chatRoomNum);
        if (optionalChatRoom.isEmpty()) {
            return List.of(); // 채팅방이 없으면 빈 리스트 반환
        }
        
        ChatRoomEntity chatRoom = optionalChatRoom.get();
        // 최신순으로 정렬된 메시지 리스트를 반환합니다.
        return chatRepository.findByChatRoomOrderByTimestampAsc(chatRoom);
    }
    
    /**
     * [관리자용] 모든 채팅방 목록을 마지막 메시지 시간 순으로 조회합니다.
     * @return 마지막 메시지 시간 내림차순으로 정렬된 ChatRoomEntity 리스트
     */
    @Transactional(readOnly = true)
    public List<ChatRoomEntity> findAllChatRoomsOrderByTime() {
        return chatRoomRepository.findAllByOrderByLastMessageTimeDesc();
    }
    
    @Transactional
    public void deleteChatRoom(Long chatRoomNum) {
        ChatRoomEntity chatRoom = chatRoomRepository.findById(chatRoomNum)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found with ID: " + chatRoomNum));
        
        // 해당 채팅방의 모든 메시지 삭제
        chatRepository.deleteAllByChatRoom(chatRoom);
        
        // 채팅방 삭제
        chatRoomRepository.delete(chatRoom);
    }
    /**
     * [관리자용] 모든 채팅방 목록을 페이지네이션하여 조회합니다.
     * @param pageable 페이징 및 정렬 정보
     * @return ChatRoomEntity의 페이지 객체
     */
    @Transactional(readOnly = true)
    public Page<ChatRoomEntity> findAllChatRoomsByPage(Pageable pageable) {
        return chatRoomRepository.findAll(pageable);
    }
    
}