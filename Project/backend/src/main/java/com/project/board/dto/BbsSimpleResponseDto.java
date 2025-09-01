package com.project.board.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder@AllArgsConstructor
@NoArgsConstructor
public class BbsSimpleResponseDto {
 private Long bulletinNum;         // 게시글 번호
 private String bbstitle;    // 제목
 private LocalDate registdate;  // 작성일
}
