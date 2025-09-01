package com.project.board.entity;

import com.project.admin.entity.AdminEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "qanda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QandAEntity {

    @Id
    private Long bulletin_num; // Bbs의 bulletin_num을 그대로 PK로 사용

    @OneToOne
    @MapsId // bulletin_num을 Bbs에서 그대로 매핑함
    @JoinColumn(name = "bulletin_num")
    private BbsEntity bbs;

    @Column(nullable = false, columnDefinition = "TEXT") //질문
    private String question;

    @Column(columnDefinition = "TEXT") // 답변
    private String answer;
    
   
}