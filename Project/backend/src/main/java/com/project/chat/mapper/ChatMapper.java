package com.project.chat.mapper;

import com.project.chat.dto.ChatMessageRequestDto;
import com.project.chat.dto.ChatMessageResponseDto;
import com.project.chat.entity.ChatMessageEntity;
import com.project.member.entity.MemberEntity;
import com.project.admin.entity.AdminEntity;
import com.project.chat.entity.ChatRoomEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "chatRoom", source = "chatRoomId", qualifiedByName = "roomIdToEntity")
    @Mapping(target = "member", source = "memberNum", qualifiedByName = "memberNumToEntity")
    @Mapping(target = "admin", source = "adminId", qualifiedByName = "adminIdToEntity")
    ChatMessageEntity toEntity(ChatMessageRequestDto dto);

    @Mapping(target = "chatRoomId", source = "chatRoom.chatRoomId")
    @Mapping(target = "memberNum", source = "member.memberNum")
    @Mapping(target = "adminId", source = "admin.adminId")
    ChatMessageResponseDto toDto(ChatMessageEntity entity);

    @Named("roomIdToEntity")
    default ChatRoomEntity roomIdToEntity(Long id) {
        if (id == null) return null;
        ChatRoomEntity r = new ChatRoomEntity();
        r.setChatRoomId(id);
        return r;
    }

    @Named("memberNumToEntity")
    default MemberEntity memberNumToEntity(Long memberNum) {
        if (memberNum == null) return null;
        MemberEntity m = new MemberEntity();
        m.setMemberNum(memberNum);
        return m;
    }

    @Named("adminIdToEntity")
    default AdminEntity adminIdToEntity(String adminId) {
        if (adminId == null) return null;
        AdminEntity a = new AdminEntity();
        a.setAdminId(adminId);
        return a;
    }
}