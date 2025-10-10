package com.djccnt15.study_springbatch.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "SETTLE_GROUP")
@NoArgsConstructor
@ToString
@SuperBuilder
public class SettleGroupEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
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
    
    public SettleGroupEntity(Long customerId, Long serviceId, Long totalCount, Long totalFee) {
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.totalCount = totalCount;
        this.totalFee = totalFee;
        this.createdAt = LocalDateTime.now();
    }
}
