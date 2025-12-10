package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "target_process")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TargetProcessEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String name;
    
    @Column(name = "process_id")
    private String processId;
    
    @Column(name = "terminated_at")
    private LocalDateTime terminatedAt;
    
    @Column
    private String status;
}
