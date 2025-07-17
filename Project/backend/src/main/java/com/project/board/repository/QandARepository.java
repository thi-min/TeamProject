package com.project.board.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.board.entity.QandAEntity;

public interface QandARepository extends JpaRepository<QandAEntity, Long> {
    Optional<QandAEntity> findByBbs_BulletinNum(Long bulletinNum);
    void deleteByBbs_BulletinNum(Long bulletinNum);
}