package com.project.board.dto;

import java.time.LocalDateTime;

import com.project.board.BoardType;

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
public class BbsDto {

    private Long bulletinNum;  // 게시글 번호

    private Long adminId;    // 관리자 아이디 

    private Long memberNum;    // 회원 번호 
    
    private String memberName; // 회원 이름 (필터링된 상태로 화면 출력용)

    private String bbsTitle;   // 제목

    private String bbsContent; // 내용

    private LocalDateTime registDate;   // 등록일

    private LocalDateTime revisionDate; // 수정일

    private LocalDateTime delDate;      // 삭제일

    private Integer viewers;    // 조회수

    private BoardType bulletinType;  // 게시판 종류(enum)

}
