package com.project.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.board.entity.QandAEntity;

public interface QandARepository extends JpaRepository<QandAEntity, Long> {
	
	// 특정 게시글 번호(bulletinNum)에 해당하는 QnA(답변) 조회
    // QnA는 게시글 1건당 1개의 답변만 존재하므로 Optional 사용
    Optional<QandAEntity> findByBbsBulletinNum(Long bulletinNum);
    
    void deleteByBbsBulletinNum(Long bulletinNum);  // 특정 게시글 번호(bulletinNum)에 연결된 QnA(답변) 삭제
}