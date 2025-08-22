package com.project.common.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "closed_day")
public class ClosedDay {

    @Id
    @Column(name = "closed_date")
    private LocalDate closedDate;
    
    @Column(name = "reason") 
    private String reason;
    
    @Column(name = "is_closed")
    private Boolean isClosed;  // true면 예약 불가
}