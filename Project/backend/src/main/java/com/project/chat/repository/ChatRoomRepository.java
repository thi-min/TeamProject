package com.project.chat.repository;

import com.project.chat.entity.ChatRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {

    /**
     * 특정 회원 번호에 해당하는 채팅방을 조회합니다.
     * @param memberNum 회원 번호
     * @return 해당 회원의 채팅방 (존재하지 않을 수 있으므로 Optional 사용)
     */
    Optional<ChatRoomEntity> findByMember_MemberNum(Long memberNum);

    /**
     * 특정 회원과 특정 관리자 간의 채팅방이 존재하는지 확인합니다.
     *
     * @param memberNum 회원 번호
     * @param adminNum 관리자 번호
     * @return 채팅방 존재 여부
     */
    boolean existsByMember_MemberNumAndAdmin_AdminNum(Long memberNum, Long adminNum);
}