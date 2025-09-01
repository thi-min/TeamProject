package com.project.alarm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.reserve.entity.Reserve;

@Repository
public interface AlarmRepository extends JpaRepository<Reserve, Long> {

	List<Reserve> findTop5ByMember_MemberNumOrderByUpdateTimeDesc(Long memberNum);
}
