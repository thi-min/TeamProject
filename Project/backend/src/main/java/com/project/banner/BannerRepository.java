package com.project.banner;

import com.project.admin.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BannerRepository extends JpaRepository<BannerEntity, Long> {

    void deleteByBannerIdIn(List<Long> ids);

    @Query("SELECT a FROM AdminEntity a WHERE a.adminId = :adminId")
    AdminEntity findAdminByAdminId(@Param("adminId") String adminId);
}
