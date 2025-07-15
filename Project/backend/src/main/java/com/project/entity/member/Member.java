package com.project.entity.member;

import com.project.entity.common.MemberState;
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
public class Member {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_num")
    private Long memberNum;

    @Column(name = "member_id", nullable = false, length = 80)
    private String memberId;

    @Column(name = "member_pw", nullable = false, length = 20)
    private String memberPw;

    @Column(name = "member_name", nullable = false, length = 12)
    private String memberName;

    @Column(name = "member_birth", nullable = false, length = 25)
    private LocalDate memberBirth;

    @Column(name = "member_sex")
    private Integer memberSex;

    @Column(name = "member_phone")
    private String memberPhone;

    @Column(name = "member_address", nullable = false, length = 255)
    private String memberAddress;

    @Column(name = "member_day")
    private LocalDate memberDay;

    @Column(name = "member_lock")
    private Boolean memberLock;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_state")
    private MemberState memberState;
    
    @Column(name = "sns_yn")
    private boolean snsYn; 

    @Column(name = "kakao_id", length = 255)
    private String kakaoId;
}