package com.djccnt15.study_springbatch.db.model;

import com.djccnt15.study_springbatch.db.model.enums.OrderStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "customer_id")
    private String customerId;
    
    @Column(name = "order_datetime")
    private LocalDateTime orderDatetime;
    
    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatusEnum status;
    
    @Column(name = "shipping_id")
    private String shippingId;
}
