package com.project.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagebbs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageBbsEntity {

    @Id
    @Column(name = "bulletin_num")
    private Long bulletinNum; // 외래키이자 기본키

    @MapsId
    @OneToOne
    @JoinColumn(name = "bulletin_num")
    private BbsEntity bbs; // 게시글 참조 (1:1 관계, bulletin_num이 Bbs의 기본키와 연결)

    @Column(name = "thumbnail_path", nullable = false, length = 255)
    private String thumbnailPath; // 썸네일 경로

    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath; // 이미지 경로
    
}