package com.project.fund.repository;

import com.example.adopt.domain.Fund;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends JpaRepository<Fund, Long> {

    // 페이징된 전체 목록 (관리자용)
    Page<Fund> findAll(Pageable pageable);

    // 스폰서명으로 검색
    Page<Fund> findByFundSponsorContainingIgnoreCase(String sponsor, Pageable pageable);
}