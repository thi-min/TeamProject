package com.project.board;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// 게시판 테이블(Bbs)에서의 게시판 종류 enum
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
public class BbsEntity {

    @Id
    @Column(name = "bulletin_num", nullable = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bulletinnum;  // 게시글 번호 (PK)

    // 관리자 아이디 (FK) - Admin 엔티티 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin adminId;

    // 회원번호 (FK) - Member 엔티티 필요
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
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
