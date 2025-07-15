package com.project.entity.board;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// 게시판 종류 enum
public enum BoardType {
    POTO, FAQ, NORMAL
}

@Entity
@Table(name = "bbs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bbs {

    @Id
    @Column(name = "bulletin_num", nullable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bulletin_num;  // 게시글 번호 (PK)

    // 관리자 아이디 (FK) - Admin 엔티티 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin_id;

    // 회원번호 (FK) - Member 엔티티 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member_num;
     
    @Column(name = "bbs_title")
    private String bbs_title; // 제목

    @Column(columnDefinition = "TEXT", nullable = false)
    private String bbs_content; // 내용

    @Column(name = "regist_date", nullable = false)
    private LocalDateTime regist_date; // 등록일

    @Column(name = "revision_date")
    private LocalDateTime revision_date; // 수정일

    @Column(name = "del_date")
    private LocalDateTime del_date;  //삭제일

    @Column(name = "viewers", nullable = false)
    private Integer Viewers = 0; // 조회수

    @Enumerated(EnumType.STRING)
    @Column(name = "bulletin_type", nullable = false, length = 10)
    private BoardType bulletin_Type; // 게시판 종류 (enum)
}
