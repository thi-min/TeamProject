package com.project.reserve.repository;

import com.project.reserve.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {

    // 예)회원번호 :3인 회원 예약 목록 가져오기
    List<Reserve> findByMember_MemberNum(Long memberNum);

    // 예약 상태로 검색
    List<Reserve> findByReserveState(com.project.reserve.entity.ReserveState reserveState);

    // 예약 날짜로 검색
    List<Reserve> findByReserveDate(LocalDate reserveDate);

    // 예약 유형별로 검색
    List<Reserve> findByReserveType(Integer reserveType);

    // 예약번호로 단건 조회 (상세 페이지)
    Optional<Reserve> findByReserveCode(Long reserveCode);
    
    //예) 특정 날짜에 소형견놀이터 예약 몇건 들어왔는지 확인
    List<Reserve> findByReserveTypeAndReserveDate(int reserveType, LocalDate date);

}
