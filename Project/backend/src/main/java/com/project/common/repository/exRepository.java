package com.project.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.member.entity.MemberEntity;

public interface exRepository extends JpaRepository<MemberEntity, Long>{
	//페이지네이션 회원목록
    //페이징 처리된 전체 회원목록 조회
    Page<MemberEntity> findAll(Pageable pageable);
    //검색 + 페이징(이름에 키워드 포함된 회원 조회)
    Page<MemberEntity> findByMemberNameContaining(String keyword, Pageable pageable);
    
    //상태 기준 조회시 필요할떄 사용
    //Page<MemberEntity> findByMemberState(MemberState state, Pageable pageable);
}
