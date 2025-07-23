package com.project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.board.entity.ImageBbsEntity;


public interface ImageBbsRepository extends JpaRepository<ImageBbsEntity, Long> {
    List<ImageBbsEntity> findByBbs_BulletinNum(Long bulletinNum);
    List<ImageBbsEntity> findAllByOrderByBulletinNumDesc();
    void deleteByBbs_BulletinNum(Long bulletinNum);
    
    // 게시글에 등록된 이미지 개수 확인용
    long countByBbs_BulletinNum(Long bulletinNum);
}