package com.djccnt15.study_springbatch.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "SETTLE_DETAIL")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettleDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id")
    private Long customerId;
    
    @Column(name = "service_id")
    private Long serviceId;
    
    @Column
    private Long count;
    
    @Column
    private Long fee;
    
    @Column(name = "target_date")
    private LocalDate targetDate;
}
