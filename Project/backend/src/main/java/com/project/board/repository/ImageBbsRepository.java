package com.project.board.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.board.entity.ImageBbsEntity;

public interface ImageBbsRepository extends JpaRepository<ImageBbsEntity, Long> {

    // 특정 게시글 번호(bulletinNum)에 해당하는 이미지 리스트 조회
    List<ImageBbsEntity> findByBbsBulletinNum(Long bulletinNum);

    // 모든 이미지 리스트를 게시글 번호 내림차순으로 정렬하여 조회
    List<ImageBbsEntity> findAllByOrderByBulletinNumDesc();

    // 특정 게시글 번호(bulletinNum)에 해당하는 이미지 전체 삭제
    void deleteByBbsBulletinNum(Long bulletinNum);

    // 특정 게시글 번호(bulletinNum)에 등록된 이미지 개수 조회
    long countByBbsBulletinNum(Long bulletinNum);

    // ----------------------------
    // 대표 이미지 조회 (엔티티 수정 없이, Y/N로 대표 여부 판단)
    @Query(value = "SELECT i.image_path FROM imagebbs i WHERE i.bulletin_num = :bulletinNum AND i.is_representative = 'Y' LIMIT 1", nativeQuery = true)
    String findRepresentativeImagePath(@Param("bulletinNum") Long bulletinNum);
    
    ImageBbsEntity findTopByBbsBulletinNumOrderByBulletinNumAsc(Long bulletinNum);
    
   
}
