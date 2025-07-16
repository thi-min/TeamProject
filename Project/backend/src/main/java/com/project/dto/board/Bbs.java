package com.project.dto.board;

import java.time.LocalDateTime;
import com.project.entity.board.BoardType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bbs {

    private Long bulletinNum;  // 게시글 번호

    private String adminId;    // 관리자 아이디 

    private Long memberNum;    // 회원 번호 

    private String bbsTitle;   // 제목

    private String bbsContent; // 내용

    private LocalDateTime registDate;   // 등록일

    private LocalDateTime revisionDate; // 수정일

    private LocalDateTime delDate;      // 삭제일

    private Integer viewers;    // 조회수

    private BoardType bulletinType;  // 게시판 종류(enum)

}
