package com.project.phoneVeify.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.member.entity.MemberEntity;
import com.project.phoneVeify.entity.PhoneAuthEntity;

public interface PhoneVeifyRepository extends JpaRepository<PhoneAuthEntity, Long>{
  
    //휴대폰 인증번호 발급여부 확인(인증기록) 조회
    Optional<PhoneAuthEntity> findByPhoneNum(String phoneNum);

}
