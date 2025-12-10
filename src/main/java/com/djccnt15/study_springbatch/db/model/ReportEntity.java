package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reports")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_type")
    private String reportType;
    
    @Column(name = "reporter_level")
    private int reporterLevel;
    
    @Column(name = "evidence_data")
    private String evidenceData;
    
    @Column(name = "reported_at")
    private LocalDateTime reportedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;
}
