package com.djccnt15.study_springbatch.db.model;

import com.djccnt15.study_springbatch.batch.practice.settlement.enums.State;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "API_ORDER")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiOrderEntity {

    @Id
    @Column(length = 100, nullable = false)
    private String id;
    
    @Column(nullable = false, name = "customer_id")
    private Long customerId;
    
    @Column(length = 100, nullable = false)
    private String url;
    
    @Column(length = 100, nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(nullable = false, name = "requested_at")
    private LocalDateTime requestedAt;
}
