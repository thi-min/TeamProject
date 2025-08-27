package com.project.reserve.repository;

import com.project.common.entity.TimeSlot;
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
    
    // 회원번호 + 예약 유형으로 필터링된 예약 리스트 가져오기 (탭)
    List<Reserve> findByMember_MemberNumAndReserveType(Long memberNum, int reserveType);
    
    // 예약 유형별로 검색
    List<Reserve> findByReserveType(Integer reserveType);

    // 예약번호로 단건 조회 (상세 페이지)
    Optional<Reserve> findByReserveCode(Long reserveCode);
    
    // 놀이터 예약 중복확인용
    boolean existsByMember_MemberNumAndLandDetail_LandDateAndLandDetail_TimeSlot_Id(
    		Long memberNum, LocalDate volDate, Long timeSlotId);

    // 봉사 예약 중복확인용
    boolean existsByMember_MemberNumAndVolunteerDetail_VolDateAndVolunteerDetail_TimeSlot_Id(
    	    Long memberNum, LocalDate volDate, Long timeSlotId);
    
    //관리자 놀이터 예약리스트 검색필터
    @Query("SELECT r FROM Reserve r " +
    	       "WHERE r.reserveType = 1 " +  // 1: 놀이터
    	       "AND (:reserveCode IS NULL OR r.reserveCode = :reserveCode) " +
    	       "AND (:memberName IS NULL OR r.member.memberName = :memberName) " +
    	       "AND ((:startDate IS NULL OR :endDate IS NULL) OR r.landDetail.landDate BETWEEN :startDate AND :endDate) " +
    	       "AND (:reserveState IS NULL OR r.reserveState = :reserveState)")
    	List<Reserve> searchLandReservations(
    	        @Param("reserveCode") Long reserveCode,
    	        @Param("memberName") String memberName,
    	        @Param("startDate") LocalDate startDate,
    	        @Param("endDate") LocalDate endDate,
    	        @Param("reserveState") ReserveState reserveState
    	);
    //관리자 봉사 예약리스트 검색필터
    @Query("SELECT r FROM Reserve r " +
    	       "WHERE r.reserveType = 2 " +  // 2: 봉사
    	       "AND (:reserveCode IS NULL OR r.reserveCode = :reserveCode) " +
    	       "AND (:memberName IS NULL OR r.member.memberName = :memberName) " +
    	       "AND ((:startDate IS NULL OR :endDate IS NULL) OR r.volunteerDetail.volDate BETWEEN :startDate AND :endDate) " +
    	       "AND (:reserveState IS NULL OR r.reserveState = :reserveState)")
    	List<Reserve> searchVolunteerReservations(
    	        @Param("reserveCode") Long reserveCode,
    	        @Param("memberName") String memberName,
    	        @Param("startDate") LocalDate startDate,
    	        @Param("endDate") LocalDate endDate,
    	        @Param("reserveState") ReserveState reserveState
    	);
    
    //관리자가 예약리스트 전체목록 조회
    @Query("SELECT r FROM Reserve r " +
    	       "JOIN FETCH r.member " +
    	       "LEFT JOIN FETCH r.landDetail " +
    	       "LEFT JOIN FETCH r.volunteerDetail")
    	List<Reserve> findAllWithDetails();
    
    // 관리자- 놀이터 특정 시간대 + 오늘 이후 날짜에 예약 존재 여부(시간대관리)
    boolean existsByLandDetail_TimeSlot_IdAndLandDetail_LandDateAfterAndReserveStateIn(Long timeSlotId, LocalDate date, List<ReserveState> states);

    // 관리자 - 봉사 특정 시간대 + 오늘 이후 날짜에 예약 존재 여부(시간대관리)
    boolean existsByVolunteerDetail_TimeSlot_IdAndVolunteerDetail_VolDateAfterAndReserveStateIn(Long timeSlotId, LocalDate date, List<ReserveState> states);
    
    // 놀이터 예약 중복검사(예약시)
    boolean existsByMember_MemberNumAndLandDetail_TimeSlot_IdAndLandDetail_LandDateAndReserveStateIn(
    	    Long memberNum,
    	    Long timeSlotId,
    	    LocalDate landDate,
    	    List<ReserveState> states
    	);

    // Volunteer 예약 중복검사(예약시)
    boolean existsByMember_MemberNumAndVolunteerDetail_TimeSlot_IdAndVolunteerDetail_VolDateAndReserveStateIn(
    	    Long memberNum,
    	    Long timeSlotId,
    	    LocalDate volDate,
    	    List<ReserveState> states
    	);
    
    // 관리자 놀이터예약 조회
    @Query("SELECT r FROM Reserve r WHERE r.reserveType = 1") // 1: 놀이터
    List<Reserve> findLandReservationsForAdmin();
    
    // 관리자 봉사예약 조회
    @Query("SELECT r FROM Reserve r WHERE r.reserveType = 2") // 2: 봉사
    List<Reserve> findVolunteerReservationsForAdmin();
    
   
}