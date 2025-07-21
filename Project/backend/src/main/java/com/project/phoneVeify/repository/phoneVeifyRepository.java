package com.project.phoneVeify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.member.entity.MemberEntity;
import com.project.phoneVeify.entity.PhoneAuthEntity;

public interface phoneVeifyRepository extends JpaRepository<PhoneAuthEntity, Long>{
  
    //휴대폰번호로 존재하는 회원이 있는지 조회
    Optional<MemberEntity> findByMemberCheck(String phoneNum, String memberNum);
    
    //휴대폰 인증번호 발송
    void certificationNumer(String phoneNumber);
    
    //휴대폰 인증번호 인증
    boolean varifyCode(String phoneNumber, String inputCode);
}
