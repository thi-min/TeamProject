package com.project.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.chat.entity.ChatMessageEntity;
import com.project.chat.entity.CheckState;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    /**
     * 특정 채팅방의 모든 메시지를 최신순으로 조회합니다.
     *
     * @param roomNum 채팅방 번호
     * @return 최신 메시지부터 정렬된 메시지 목록
     */
    List<ChatMessageEntity> findByChatRoom_RoomNumOrderBySentDateDesc(Long roomNum);

    /**
     * 특정 채팅방의 확인되지 않은 메시지(CheckState.N)의 수를 조회합니다.
     *
     * @param roomNum 채팅방 번호
     * @return 미확인 메시지 수
     */
    long countByChatRoom_RoomNumAndCheckState(Long roomNum, CheckState checkState);
    /**
     * 특정 채팅방의 특정 확인 상태를 가진 메시지 목록을 조회합니다.
     * @param roomNum 채팅방 번호
     * @param checkState 확인 상태 (예: Y, N)
     * @return 해당 조건에 맞는 메시지 목록
     */
    List<ChatMessageEntity> findByChatRoom_RoomNumAndCheckState(Long roomNum, CheckState checkState);
}