package com.project.reserve.repository;

import com.project.reserve.dto.AdminReservationListDto;
import com.project.reserve.entity.Reserve;
import com.project.reserve.entity.ReserveState;

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
    
    // 예약 유형별로 검색
    List<Reserve> findByReserveType(Integer reserveType);

    // 예약번호로 단건 조회 (상세 페이지)
    Optional<Reserve> findByReserveCode(Long reserveCode);

    //관리자 예약리스트 검색필터
    @Query("SELECT r FROM Reserve r " +
    	       "WHERE (:reserveCode IS NULL OR r.reserveCode = :reserveCode) " +
    	       "AND (:memberName IS NULL OR r.member.memberName = :memberName) " +
    	       "AND ((:startDate IS NULL OR :endDate IS NULL) OR r.reserveDate BETWEEN :startDate AND :endDate)" +
    		   "AND (:reserveState IS NULL OR r.reserveState = :reserveState)")
    	List<Reserve> searchBar(@Param("reserveCode") Long reserveCode,
    	        				@Param("memberName") String memberName,
    	        				@Param("startDate") LocalDate startDate,
    	        				@Param("endDate") LocalDate endDate,
    							@Param("reserveState") ReserveState reserveState);
    
    //관리자가 예약리스트 목록 조회
    @Query("SELECT new com.project.reserve.dto.AdminReservationListDto(" +
    	       "r.reserveCode, m.memberName, r.programName, r.reserveDate, r.reserveState) " +
    	       "FROM Reserve r JOIN r.member m")
    	List<AdminReservationListDto> findAllReservationsForAdmin();
}
