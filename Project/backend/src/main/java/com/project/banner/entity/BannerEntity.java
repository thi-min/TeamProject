package com.project.banner.entity;

import com.project.admin.entity.AdminEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerEntity {

    @Id // 기본 키(primary key) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id", nullable = false)
    private Long bannerId; // 배너 고유 ID

    // 배너 제목
    @Column(name = "title", nullable = false)
    private String title;

    // 배너 부제목
    @Column(name = "sub_title")
    private String subTitle;

    // 이미지 경로
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    // 이미지 대체 텍스트
    @Column(name = "alt_text")
    private String altText;

    // 링크
    @Column(name = "link_url")
    private String linkUrl;

    // 배너 노출 시작일
    @Column(name = "start_date")
    private LocalDate startDate;

    // 배너 노출 종료일
    @Column(name = "end_date")
    private LocalDate endDate;

    // 배너 노출 여부
    @Column(name = "visible", nullable = false)
    private Boolean visible;

    // 배너 생성일시
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 배너 수정일시
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY) //N:1관계
    @JoinColumn(name = "admin_num", referencedColumnName = "admin_num", nullable = false) // 외래키(FK) 설정
    private AdminEntity admin; // 배너를 등록한 관리자
}