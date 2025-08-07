package com.project.board.dto;

import lombok.*;
import java.io.*;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "dtoBuilder")  // 기본 빌더는 dtoBuilder()로 변경
public class FileUpLoadDto {

    private Long fileNum;       // 파일 번호 (기본키)

    private Long bulletinNum;   // 게시글 번호 (Bbs 엔티티의 PK)

    private String originalName; // 원본 파일명

    private String savedName;    // 저장된 파일명

    private String path;         // 파일 경로

    private Long size;           // 파일 크기

    private String extension;    // 확장자
    
    private MultipartFile file;  // 실제 업로드 되는 파일 데이터
    
    public static class FileUpLoadDtoBuilder {
        public FileUpLoadDtoBuilder originalName(String originalName) {
            this.originalName = originalName;

            // 확장자 자동 추출 후 소문자로 세팅
            if (originalName != null && originalName.contains(".")) {
                int lastDotIndex = originalName.lastIndexOf('.');
                this.extension = originalName.substring(lastDotIndex + 1).toLowerCase();
            } else {
                this.extension = null;
            }

            return this;
        }
    }
}