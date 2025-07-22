package com.project.banner;

import com.project.admin.AdminEntity;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "banner_id", nullable = false)
    private Long bannerId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "sub_title", nullable = true)
    private String subTitle;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "alt_text", nullable = true)
    private String altText;

    @Column(name = "link_url", nullable = true)
    private String linkUrl;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "visible", nullable = false)
    private Boolean visible;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminEntity admin;
}

