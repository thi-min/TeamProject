package com.project.reserve.repository;

import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.entity.Reserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReserveRepository extends JpaRepository<Reserve, Long> {
	
	// 예)회원번호 :3인 회원 예약 목록 가져오기
    List<Reserve> findByMember_MemberNum(Long memberNum);
    
    // 예약 상태로 검색
    List<Reserve> findByReserveState(com.project.reserve.entity.ReserveState reserveState);

    // 예약 유형별로 검색
    List<Reserve> findByReserveType(Integer reserveType);

    // 예약번호로 단건 조회 (상세 페이지)
    Optional<Reserve> findByReserveCode(Long reserveCode);
    
    //시작일과 종료일 사이의 예약 목록 조회
    List<Reserve> findByReserveDateBetween(LocalDate startDate, LocalDate endDate);

    //검색창
    @Query("SELECT r FROM Reserve r WHERE (:reserveCode IS NULL OR r.reserveCode = :reserveCode) " +
    	       "AND (:memberName IS NULL OR r.member.memberName = :memberName) " + //null값이라면 무시, 검색창에 데이터가 입력됐다면 memberName 대조
    	       "AND (:reserveDate IS NULL OR r.reserveDate = :reserveDate)")
    	List<Reserve> searchBar(@Param("reserveCode") Long reserveCode,
    	                        @Param("memberName") String memberName,
    	                        @Param("reserveDate") LocalDate reserveDate);
    //관리자가 예약리스트 조회
    @Query("SELECT new com.project.reserve.dto.AdminReservationListDto(" +
    	       "r.reserveCode, m.memberName, r.programName, r.reserveDate, r.reserveState) " +
    	       "FROM Reserve r JOIN r.member m")
    	List<AdminReservationListDto> findAllReservationsForAdmin();
}
