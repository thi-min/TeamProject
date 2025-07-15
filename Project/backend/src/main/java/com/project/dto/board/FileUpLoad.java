package com.project.dto.board;

import lombok.*;
import java.io.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUpLoad implements Serializable {

    private Long fileNum;       // 파일 번호 (기본키)

    private Long bulletinNum;   // 게시글 번호 (Bbs 엔티티의 PK)

    private String originalName; // 원본 파일명

    private String savedName;    // 저장된 파일명

    private String path;         // 파일 경로

    private Long size;           // 파일 크기

    private String extension;    // 확장자
}
