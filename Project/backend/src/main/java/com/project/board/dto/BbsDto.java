package com.project.board.dto;

import java.time.LocalDateTime;
import com.project.board.BoardType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BbsDto {

    private Long bulletinNum;      // 게시글 번호
    private String adminId;        // 관리자 아이디 
    private Long memberNum;        // 회원 번호 
    private String memberName;     // 회원 이름 (필터링된 상태로 화면 출력용)
    private String bbsTitle;       // 제목
    private String bbsContent;     // 내용
    private LocalDateTime registDate;   // 등록일
    private LocalDateTime revisionDate; // 수정일
    private LocalDateTime delDate;      // 삭제일
    private Integer viewers;       // 조회수
    private BoardType bulletinType; // 게시판 종류(enum)

    // JPQL new BbsDto(...) 생성자
    public BbsDto(
            Long bulletinNum,
            String bbstitle,
            String bbscontent,
            BoardType bulletinType,
            String memberName,
            LocalDateTime registdate
    ) {
        this.bulletinNum = bulletinNum;
        this.bbsTitle = bbstitle;
        this.bbsContent = bbscontent;
        this.bulletinType = bulletinType;
        this.memberName = memberName;
        this.registDate = registdate;
    }
}
