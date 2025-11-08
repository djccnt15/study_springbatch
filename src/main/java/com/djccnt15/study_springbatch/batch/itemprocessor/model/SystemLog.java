package com.djccnt15.study_springbatch.batch.itemprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {
    
    private Long userId;
    
    private String rawCommand;
    
    private LocalDateTime executedAt;
}
