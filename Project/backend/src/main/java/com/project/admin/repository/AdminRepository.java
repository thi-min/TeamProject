package com.project.admin.repository;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.admin.entity.AdminEntity;
import com.project.member.entity.MemberEntity;

public interface AdminRepository extends JpaRepository<AdminEntity, Long>{
	//관리자 로그인용 id + pw 조회
	Optional<AdminEntity> findByAdminIdAndAdminPw(String adminId, String adminPw);
	
	//관리자 비밀번호 변경
    //비밀번호 변경
    Optional<AdminEntity> adminchangeByPassword(String adminId);
}
