package com.project.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "time_slot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;	//시간대 당 id부여 

    @Column(name = "label", nullable = false)
    private String label;  // 예: "09:00 ~ 11:00"

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;	//시작 시각 ex) 09:00

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;		//종료 시각 ex)11:00
    
    @Column(name = "capacity", nullable = false)
    private int capacity; // 정원 (도메인별로 다른 정원 설정 가능)

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;  // 이용가능여부 기본값 true
    
    @Enumerated(EnumType.STRING)
    @Column(name = "time_type", nullable = false)
    private TimeType timeType;
    
    //label 자동생성
    @PrePersist
    @PreUpdate
    public void generateLabel() {
        if (startTime != null && endTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            this.label = startTime.format(formatter) + " ~ " + endTime.format(formatter);
        }
    }
}