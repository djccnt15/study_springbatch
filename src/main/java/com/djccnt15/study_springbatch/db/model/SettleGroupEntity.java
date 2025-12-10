package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "SETTLE_GROUP")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SettleGroupEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id")
    private Long customerId;
    
    @Column(name = "service_id")
    private Long serviceId;
    
    @Column(name = "total_count")
    private Long totalCount;
    
    @Column(name = "total_fee")
    private Long totalFee;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
