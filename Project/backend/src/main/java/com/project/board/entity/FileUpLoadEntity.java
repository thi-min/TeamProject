package com.project.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_upload") // 테이블명은 일반적으로 소문자 사용 추천
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUpLoadEntity {

    @Id
    @Column(name = "file_num", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long filenum; // 파일 번호 (기본키)

    // 게시글 번호 (외래키)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bulletinnum", nullable = false)
    private BbsEntity bbs;

    @Column(name = "ori_file_name", nullable = false)
    private String originalName; // 원본 파일명

    @Column(name = "saved_name", nullable = false)
    private String savedName; // 저장된 파일명

    @Column(name = "file_path", nullable = false)
    private String path; // 파일 경로

    @Column(name = "file_size", nullable = false)
    private Long size; // 파일 크기 

    @Column(name = "extension", nullable = false)
    private String extension; // 확장자
}