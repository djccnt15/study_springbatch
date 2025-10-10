package com.djccnt15.study_springbatch.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "SETTLE_DETAIL")
@NoArgsConstructor
@ToString
@SuperBuilder
public class SettleDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
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
