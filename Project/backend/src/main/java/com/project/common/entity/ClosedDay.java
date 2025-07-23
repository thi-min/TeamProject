package com.project.common.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "closed_day")
public class ClosedDay {

    @Id
    @Column(name = "closed_date")
    private LocalDate closedDate;

    @Column(name = "is_closed")
    private Boolean isClosed;  // true면 예약 불가
}