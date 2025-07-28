package com.project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.board.entity.FileUpLoadEntity;

public interface FileUpLoadRepository extends JpaRepository<FileUpLoadEntity, Long> {
	
    List<FileUpLoadEntity> findByBbsBulletinNum(Long bulletinNum); // 특정 게시글 번호(bulletinNum)에 해당하는 모든 첨부파일 리스트 조회
    
    void deleteByBbsBulletinNum(Long bulletinNum); // 특정 게시글 번호(bulletinNum)에 해당하는 모든 첨부파일 삭제
}