package com.project.volunteer.repository;

import com.project.volunteer.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    // 예약 코드로 봉사 정보 조회
    Optional<Volunteer> findByReserveCode(Long reserveCode);
}