package com.project.board.entity;

import java.time.LocalDateTime;

import com.project.board.BoardType;
import com.project.member.entity.MemberEntity;
import com.project.admin.entity.AdminEntity; 

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "bbs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BbsEntity {

    @Id
    @Column(name = "bulletin_num", nullable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bulletinNum;  // 게시글 번호 (PK)

    // 관리자 아이디 (FK) - Admin 엔티티 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = true)
    private AdminEntity adminId;

    // 회원번호 (FK) - Member 엔티티 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", nullable = true)
    private MemberEntity memberNum;
     
    @Column(name = "bbs_title")
    private String bbstitle; // 제목

    @Column(name = "bbs_content", columnDefinition = "TEXT", nullable = false)
    private String bbscontent; // 내용

    @Column(name = "regist_date", nullable = false)
    private LocalDateTime registdate; // 등록일

    @Column(name = "revision_date")
    private LocalDateTime revisiondate; // 수정일

    @Column(name = "del_date")
    private LocalDateTime deldate;  //삭제일

    @Column(name = "viewers", nullable = false)
    private Integer viewers = 0; // 조회수

    @Enumerated(EnumType.STRING)
    @Column(name = "bulletin_type", nullable = false, length = 10)
    private BoardType bulletinType; // 게시판 종류 (enum)
    
}