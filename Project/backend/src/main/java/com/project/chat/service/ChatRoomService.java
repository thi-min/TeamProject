package com.project.chat.service;

import com.project.chat.entity.ChatRoomEntity;
import com.project.chat.repository.ChatRoomRepository;
import com.project.member.repository.MemberRepository;
import com.project.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public ChatRoomEntity createRoomForMember(Long memberNum) {
        ChatRoomEntity room = new ChatRoomEntity();
        com.project.member.entity.MemberEntity m = memberRepository.findByMemberNum(memberNum)
                .orElseGet(() -> {
                    com.project.member.entity.MemberEntity mm = new com.project.member.entity.MemberEntity();
                    mm.setMemberNum(memberNum);
                    return mm;
                });
        room.setMember(m);
        room.setCreateAt(LocalDateTime.now());
        return chatRoomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public ChatRoomEntity getRoom(Long roomId) {
        return chatRoomRepository.findById(roomId).orElse(null);
    }
}