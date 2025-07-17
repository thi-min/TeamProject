package com.project.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {
	
	@Id //기본키
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_num")
    private Long memberNum; //회원번호

    @Column(name = "member_id", nullable = false, length = 80)
    private String memberId; //이메일 아이디

    @Column(name = "member_pw", nullable = false, length = 20)
    private String memberPw; //비밀번호

    @Column(name = "member_name", nullable = false, length = 12)
    private String memberName; //이름

    @Column(name = "member_birth", nullable = false, length = 25)
    private String memberBirth; //생년월일

    @Column(name = "member_phone")
    private String memberPhone; //휴대폰 번호

    @Column(name = "member_address", nullable = false, length = 255)
    private String memberAddress; //주소

    @Column(name = "member_day")
    private LocalDate memberDay; //가입일시

    @Column(name = "member_lock")
    private Boolean memberLock; //계정 잠금 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "member_sex")
    private MemberSex memberSex; //성별
    
    @Enumerated(EnumType.STRING)
    @Column(name = "member_state")
    private MemberState memberState; //회원상태
    
    @Column(name = "sns_yn")
    private boolean snsYn; //문자 수신여부(동의/비동의)

    @Column(name = "kakao_id", length = 255)
    private String kakaoId; //카카오아이디
}