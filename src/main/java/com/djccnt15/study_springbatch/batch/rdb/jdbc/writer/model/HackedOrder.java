package com.djccnt15.study_springbatch.batch.rdb.jdbc.writer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HackedOrder {
    
    private Long id;
    
    private Long customerId;
    
    private LocalDateTime orderDateTime;
    
    private String status;
    
    private String shippingId;
}
