package com.project.chat.service;

import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessageEntity saveMessage(ChatRoomEntity room, Long memberNum, String adminId, String content) {
        ChatMessageEntity msg = new ChatMessageEntity();
        msg.setChatRoom(room);
        // memberNum/adminId은 선택적. MemberEntity/AdminEntity가 있으면 실제 엔티티를 set 해 주세요.
        if (memberNum != null) {
            com.project.member.entity.MemberEntity m = new com.project.member.entity.MemberEntity();
            m.setMemberNum(memberNum);
            msg.setMember(m);
        }
        if (adminId != null) {
            com.project.admin.entity.AdminEntity a = new com.project.admin.entity.AdminEntity();
            // AdminEntity의 PK 혹은 ID 필드가 String이면 아래처럼 세팅하고, 실제 DB 연동 시 repository로 조회해서 set 권장
            a.setAdminId(adminId);
            msg.setAdmin(a);
        }
        msg.setChatCont(content);
        msg.setSendTime(LocalDateTime.now());
        msg.setChatCheck(com.project.common.enums.CheckState.N);
        return chatMessageRepository.save(msg);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageEntity> getMessagesByRoom(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomChatRoomIdOrderBySendTimeAsc(chatRoomId);
    }
}