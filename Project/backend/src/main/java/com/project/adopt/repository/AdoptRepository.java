package com.project.adopt.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.adopt.entity.AdoptEntity;
import com.project.adopt.entity.AdoptState;

@Repository
public interface AdoptRepository extends JpaRepository<AdoptEntity, Long> {
	
    List<AdoptEntity> findByMemberMemberNum(Long memberNum);//회원번호로 조회
    
    List<AdoptEntity> findByAdoptState(AdoptState state);//입양상태로 조회
    
    List<AdoptEntity> findByMember_MemberNum(Long memberNum);//개인 입양 조회
    
    Page<AdoptEntity> findByMember_MemberNum(Long memberNum, Pageable pageable);
}