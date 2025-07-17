package com.project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.board.entity.ImageBbsEntity;


public interface ImageBbsRepository extends JpaRepository<ImageBbsEntity, Long> {
    List<ImageBbsEntity> findByBbs_BulletinNum(Long bulletinNum);
    List<ImageBbsEntity> findAllByOrderByBulletinNumDesc();
    void deleteByBbs_BulletinNum(Long bulletinNum);
}