package com.project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.board.entity.FileUpLoadEntity;

public interface FileUpLoadRepository extends JpaRepository<FileUpLoadEntity, Long> {
    List<FileUpLoadEntity> findByBbs_BulletinNum(Long bulletinNum);
    void deleteByBbs_BulletinNum(Long bulletinNum);
}