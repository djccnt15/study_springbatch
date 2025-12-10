package com.djccnt15.study_springbatch.db.model;

import com.djccnt15.study_springbatch.db.model.enums.ActivityTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "activities")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private double severityIndex;
    
    @Column
    private LocalDate detectionDate;
    
    @Column
    @Enumerated(EnumType.STRING)
    private ActivityTypeEnum activityType;
    
    @Column
    private String location;
    
    @ManyToOne
    @JoinColumn(name = "human_id")
    @ToString.Exclude
    private HumanEntity humanEntity;
}
