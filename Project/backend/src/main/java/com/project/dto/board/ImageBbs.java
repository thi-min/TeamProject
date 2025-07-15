package com.project.dto.board;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageBbs {

    private Long bulletinNum;      // 게시글 번호 (PK)

    private String thumbnailPath;  // 썸네일 경로

    private String imagePath;      // 이미지 경로
}
