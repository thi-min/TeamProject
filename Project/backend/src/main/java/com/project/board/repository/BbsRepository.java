package com.project.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.admin.entity.AdminEntity;
import com.project.board.BoardType;
import com.project.board.dto.BbsDto;
import com.project.board.dto.FileUpLoadDto;
import com.project.board.entity.BbsEntity;
import com.project.member.entity.MemberEntity;

public interface BbsRepository extends JpaRepository<BbsEntity, Long> {

    List<BbsEntity> findByBulletinType(BoardType type);

    List<BbsEntity> findByAdminId(AdminEntity adminId);

    List<BbsEntity> findByMemberNum(MemberEntity memberNum);

    Page<BbsEntity> findByBulletinType(BoardType type, Pageable pageable);

    Page<BbsEntity> findByBbstitleContaining(String bbstitle, Pageable pageable);

    Page<BbsEntity> findByMemberNum_MemberNameContaining(String memberName, Pageable pageable);

    Page<BbsEntity> findByBbscontentContaining(String bbscontent, Pageable pageable);

    Page<BbsEntity> findByBulletinTypeAndBbstitleContainingAndBbscontentContaining(BoardType bulletinType, String bbstitle, String bbscontent, Pageable pageable);

    Page<BbsEntity> findByBbstitleContainingAndBbscontentContaining(String bbstitle, String bbscontent, Pageable pageable);

    Page<BbsEntity> findByBulletinTypeAndBbstitleContaining(BoardType type, String bbstitle, Pageable pageable);

    Page<BbsEntity> findByBulletinTypeAndBbscontentContaining(BoardType type, String bbscontent, Pageable pageable);

    //25.09.01 안형주 추가
    List<BbsEntity> findTop5ByBulletinTypeOrderByRegistdateDesc(BoardType boardType);
    
    // ---------------- JPQL for Admin QnA / FAQ ----------------
    @Query("""
        SELECT new com.project.board.dto.BbsDto(
            b.bulletinNum,
            b.bbstitle,
            b.bbscontent,
            b.bulletinType,
            COALESCE(m.memberName, a.adminName),
            b.registdate
        )
        FROM BbsEntity b
        LEFT JOIN b.memberNum m
        LEFT JOIN b.adminId a
        WHERE b.bulletinType = :type
          AND (:bbstitle IS NULL OR b.bbstitle LIKE CONCAT('%', :bbstitle, '%'))
          AND (:memberName IS NULL OR COALESCE(m.memberName, a.adminName) LIKE CONCAT('%', :memberName, '%'))
          AND (:bbscontent IS NULL OR b.bbscontent LIKE CONCAT('%', :bbscontent, '%'))
        ORDER BY b.registdate DESC
    """)
    Page<BbsDto> findBbsByTypeAndSearch(
        @Param("type") BoardType type,
        @Param("bbstitle") String bbstitle,
        @Param("memberName") String memberName,
        @Param("bbscontent") String bbscontent,
        Pageable pageable
    );

    @Query("""
        SELECT COUNT(b)
        FROM BbsEntity b
        LEFT JOIN b.memberNum m
        LEFT JOIN b.adminId a
        WHERE b.bulletinType = :type
          AND (:bbstitle IS NULL OR b.bbstitle LIKE CONCAT('%', :bbstitle, '%'))
          AND (:memberName IS NULL OR COALESCE(m.memberName, a.adminName) LIKE CONCAT('%', :memberName, '%'))
          AND (:bbscontent IS NULL OR b.bbscontent LIKE CONCAT('%', :bbscontent, '%'))
    """)
    long countBbsByTypeAndSearch(
        @Param("type") BoardType type,
        @Param("bbstitle") String bbstitle,
        @Param("memberName") String memberName,
        @Param("bbscontent") String bbscontent
    );

    // ---------------- 첨부파일 조회 ----------------
    @Query("""
        SELECT new com.project.board.dto.FileUpLoadDto(
            f.filenum, f.bbs.bulletinNum, f.originalName, f.savedName, f.path, f.size, f.extension, null
        )
        FROM FileUpLoadEntity f
        WHERE f.bbs.bulletinNum = :bulletinNum
        ORDER BY f.filenum ASC
    """)
    List<FileUpLoadDto> findFilesByBulletinNum(@Param("bulletinNum") Long bulletinNum);

    @Query("""
        SELECT new com.project.board.dto.FileUpLoadDto(
            f.filenum, f.bbs.bulletinNum, f.originalName, f.savedName, f.path, f.size, f.extension, null
        )
        FROM FileUpLoadEntity f
        WHERE f.filenum = :fileId
    """)
    Optional<FileUpLoadDto> findFileById(@Param("fileId") Long fileId);
}
