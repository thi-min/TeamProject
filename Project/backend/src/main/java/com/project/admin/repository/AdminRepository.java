package com.project.admin.repository;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.admin.entity.AdminEntity;
import com.project.member.entity.MemberEntity;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, Long>{
	//관리자 로그인용 id + pw 조회
	Optional<AdminEntity> findByAdminIdAndAdminPw(String adminId, String adminPw);
	
	//관리자 비밀번호 변경
    //비밀번호 변경
	//예시: adminId로 해당 관리자 조회
	//Optional<AdminEntity> findByAdminId(String adminId);
	Optional<AdminEntity> findFirstByAdminId(String adminId);
	// ✅ 서비스에서 사용하는 검색 메서드 (이게 반드시 있어야 함)
	 // 이름 부분 일치 검색 (페이지네이션)
    Page<MemberEntity> findByMemberNameContaining(String keyword, Pageable pageable);

    // 상세 조회에 사용 (memberNum이 PK가 아니면 해당 컬럼명에 맞춘 메서드 추가)
    Optional<MemberEntity> findByMemberNum(Long memberNum);
}

