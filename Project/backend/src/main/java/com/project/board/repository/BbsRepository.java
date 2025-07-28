package com.project.board.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.admin.entity.AdminEntity;
import com.project.board.BoardType;
import com.project.board.entity.BbsEntity;
import com.project.member.entity.MemberEntity;

public interface BbsRepository extends JpaRepository<BbsEntity, Long> {

	@Query
	List<BbsEntity> findByBulletinType(BoardType type); // 특정 게시판 타입의 모든 게시글 리스트 조회
	
    List<BbsEntity> findByAdminId(AdminEntity adminId); // 관리자 ID로 작성한 게시글 리스트 조회
    
    List<BbsEntity> findByMemberNum(MemberEntity memberNum);  // 회원 번호로 작성한 게시글 리스트 조회

    Page<BbsEntity> findByBulletinType(BoardType type, Pageable pageable); // 특정 게시판 타입의 게시글을 페이지 단위로 조회
    
    Page<BbsEntity> findByBbstitleContaining(String bbstitle, Pageable pageable); // 제목에 키워드가 포함된 게시글을 페이지 단위로 조회 (제목 검색)
    
   // Page<BbsEntity> findByMember_MemberNameContaining(String bbstitle, Pageable pageable);  // 작성자 이름에 키워드가 포함된 게시글을 페이지 단위로 조회
    
    Page<BbsEntity> findByBbscontentContaining(String bbscontent, Pageable pageable);  // 게시글 내용에 키워드가 포함된 게시글을 페이지 단위로 조회 (내용 검색)
    
    Page<BbsEntity> findByBbstitleContainingOrBbscontentContaining(String bbstitle, String bbscontent, Pageable pageable); // 제목 또는 내용에 키워드가 포함된 게시글을 페이지 단위로 조회

    Page<BbsEntity> findByBulletinTypeAndBbstitleContaining(BoardType type, String bbstitle, Pageable pageable); // 게시판 타입 + 제목 검색 (AND 조건)
    
   // Page<BbsEntity> findByBulletinTypeAndMember_MemberNameContaining(BoardType type, String keyword, Pageable pageable); // 게시판 타입 + 작성자 이름 검색 (AND 조건)
    
    Page<BbsEntity> findByBulletinTypeAndBbscontentContaining(BoardType type, String bbscontent, Pageable pageable); // 게시판 타입 + 내용 검색 (AND 조건)

 // 게시판 타입 + (제목 OR 내용) 검색 (복합 조건) → 직접 JPQL 쿼리로 명확하게 작성
    @Query("SELECT b FROM BbsEntity b WHERE b.bulletinType = :type AND (b.bbstitle LIKE %:keyword% OR b.bbscontent LIKE %:keyword%)")
    Page<BbsEntity> findByBulletinTypeAndTitleOrContent(   @Param("type") BoardType type,
    	    @Param("bbstitle") String bbstitle,
    	    @Param("bbscontent") String bbscontent,
    	    Pageable pageable);
    
}