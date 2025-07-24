package com.project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.board.entity.ImageBbsEntity;


public interface ImageBbsRepository extends JpaRepository<ImageBbsEntity, Long> {
	
    List<ImageBbsEntity> findByBbs_BulletinNum(Long bulletinNum); // 특정 게시글 번호(bulletinNum)에 해당하는 이미지 리스트 조회
    
    List<ImageBbsEntity> findAllByOrderByBulletinNumDesc(); // 모든 이미지 리스트를 게시글 번호 내림차순으로 정렬하여 조회
    
    void deleteByBbs_BulletinNum(Long bulletinNum); // 특정 게시글 번호(bulletinNum)에 해당하는 이미지 전체 삭제
    
    long countByBbs_BulletinNum(Long bulletinNum);  // 특정 게시글 번호(bulletinNum)에 등록된 이미지 개수 조회
}