package com.project.land.repository;

import com.project.land.entity.Land;
import com.project.land.entity.LandType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LandRepository extends JpaRepository<Land, Long> {

    // 1:1 매핑된 Reserve 기준으로 조회
    Optional<Land> findByReserveCode(Long reserveCode);

    // 필요시 놀이터 타입별 조회
    List<Land> findByLandType(LandType landType);
}