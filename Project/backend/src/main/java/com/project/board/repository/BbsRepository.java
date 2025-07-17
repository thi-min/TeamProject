package com.project.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.board.BoardType;
import com.project.board.entity.BbsEntity;

public interface BbsRepository extends JpaRepository<BbsEntity, Long> {
	List<BbsEntity> findByBulletinType(BoardType type);
    List<BbsEntity> findByAdmin_AdminId(String adminId);
    List<BbsEntity> findByMember_MemberNum(Long memberNum);

    Page<BbsEntity> findByBulletinType(BoardType type, Pageable pageable);
    Page<BbsEntity> findByBbstitleContaining(String keyword, Pageable pageable);
    Page<BbsEntity> findByMember_MemberNameContaining(String keyword, Pageable pageable);
    Page<BbsEntity> findByBbscontentContaining(String keyword, Pageable pageable);
    Page<BbsEntity> findByBbstitleContainingOrBbscontentContaining(String keyword1, String keyword2, Pageable pageable);

    Page<BbsEntity> findByBulletinTypeAndBbstitleContaining(BoardType type, String keyword, Pageable pageable);
    Page<BbsEntity> findByBulletinTypeAndMember_MemberNameContaining(BoardType type, String keyword, Pageable pageable);
    Page<BbsEntity> findByBulletinTypeAndBbscontentContaining(BoardType type, String keyword, Pageable pageable);

    // 복합 조건 (title or content) + type → 직접 JPQL 쿼리로 명확하게 작성
    @Query("SELECT b FROM BbsEntity b WHERE b.bulletinType = :type AND (b.bbstitle LIKE %:keyword% OR b.bbscontent LIKE %:keyword%)")
    Page<BbsEntity> findByBulletinTypeAndTitleOrContent(@Param("type") BoardType type, @Param("keyword") String keyword, Pageable pageable);
    
}