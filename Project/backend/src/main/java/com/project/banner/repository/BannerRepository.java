package com.project.banner.repository;

import com.project.admin.entity.AdminEntity;
import com.project.banner.entity.BannerEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {

	// bannerId 리스트에 해당하는 배너들을 일괄 삭제
    void deleteByBannerIdIn(List<Long> ids);

    // 관리자 ID로 AdminEntity 조회 (직접 JPQL로 정의한 쿼리)
    @Query("SELECT a FROM AdminEntity a WHERE a.adminId = :adminId")
    AdminEntity findAdminByAdminId(@Param("adminId") String adminId);
}
