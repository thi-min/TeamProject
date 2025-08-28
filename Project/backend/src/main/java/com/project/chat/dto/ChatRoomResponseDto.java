package com.project.chat.dto;

import com.project.chat.entity.ChatRoomEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {
    private Long roomNum;
    private Long memberNum;
    private String memberName;
    private Long adminNum;
    private String adminName;

    public static ChatRoomResponseDto fromEntity(ChatRoomEntity chatRoom) {
        return ChatRoomResponseDto.builder()
                .roomNum(chatRoom.getRoomNum())
                .memberNum(chatRoom.getMember().getMemberNum())
                .memberName(chatRoom.getMember().getMemberName())
                .adminNum(chatRoom.getAdmin().getAdminNum())
                .adminName(chatRoom.getAdmin().getAdminName())
                .build();
    }
}