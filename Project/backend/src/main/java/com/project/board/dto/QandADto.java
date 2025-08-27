package com.project.board.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QandADto {

    private Long bulletinNum;  // 게시글 번호 (PK)
    private String adminId;
    private String question;   // 질문

    private String answer;     // 답변 (null 가능)
}