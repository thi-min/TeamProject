package com.project.chat.service;

import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.mapper.ChatMapper;
import com.project.chat.repository.ChatMessageRepository;
import com.project.chat.repository.ChatRoomRepository;
import com.project.member.repository.MemberRepository;
import com.project.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final ChatMapper chatMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatRoomEntity createRoomForMember(Long memberNum) {
        ChatRoomEntity room = new ChatRoomEntity();
        room.setCreateAt(LocalDateTime.now());
        // set member entity if exists (or set with id only)
        memberRepository.findByMemberNum(memberNum).ifPresent(room::setMember);
        return chatRoomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public ChatRoomEntity getRoom(Long id) {
        return chatRoomRepository.findById(id).orElse(null);
    }

    @Transactional
    public ChatMessageEntity saveMessage(ChatMessageEntity msg) {
        if (msg.getSendTime() == null) msg.setSendTime(LocalDateTime.now());
        if (msg.getChatCheck() == null) msg.setChatCheck(com.project.common.enums.CheckState.N);
        ChatMessageEntity saved = chatMessageRepository.save(msg);

        // broadcast to topic
        messagingTemplate.convertAndSend("/topic/chat/" + saved.getChatRoom().getChatRoomId(),
                chatMapper.toDto(saved));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ChatMessageEntity> getMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomChatRoomIdOrderBySendTimeAsc(chatRoomId);
    }
    @Transactional(readOnly = true)
    public List<ChatRoomEntity> getAllRooms() {
        return chatRoomRepository.findAll();
}
}