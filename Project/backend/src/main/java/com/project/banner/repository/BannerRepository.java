package com.project.banner.repository;

import com.project.admin.entity.AdminEntity;
import com.project.banner.entity.BannerEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {

	// bannerId 리스트에 해당하는 배너들을 일괄 삭제
    void deleteByBannerIdIn(List<Long> ids);
    
    // 사용자 노출용 (메인페이지)
    // List<BannerEntity> findByVisibleTrueAndStartDateBeforeAndEndDateAfter(LocalDate today1, LocalDate today2);
    
    // 관리자용 전체배너 조회
    Page<BannerEntity> findAll(Pageable pageable);
    
    //25.09.01 안형주 추가
    //활성상태 베너 조회
    List<BannerEntity> findByVisibleTrue();
    
    // 사용자 노출용 (메인페이지)
    @Query("SELECT b FROM BannerEntity b " +
    	       "WHERE b.visible = true " +
    	       "AND b.startDate <= :today " +
    	       "AND (b.endDate IS NULL OR b.endDate >= :today)")
    	List<BannerEntity> findActiveBanners(@Param("today") LocalDate today);
}