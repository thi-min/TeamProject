package com.project.fund.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.fund.entity.FundEntity;

@Repository
public interface FundRepository extends JpaRepository<FundEntity, Long> {

    // 페이징된 전체 목록 (관리자용)
    Page<FundEntity> findAll(Pageable pageable);

    // 스폰서명으로 검색
    Page<FundEntity> findByFundSponsorContainingIgnoreCase(String sponsor, Pageable pageable);

    // FundServiceImpl에서 사용되는 메서드를 추가
    Page<FundEntity> findByFundSponsorContaining(String fundSponsor, Pageable pageable);
    
    Page<FundEntity> findByMember_MemberNum(Long memberNum, Pageable pageable); 

}