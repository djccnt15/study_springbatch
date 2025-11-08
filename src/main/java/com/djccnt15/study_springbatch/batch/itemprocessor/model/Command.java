package com.djccnt15.study_springbatch.batch.itemprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Command {
    
    private String commandText;
    
    private String userId;
    
    private String targetProcess;
    
    private static final Random RANDOM = new Random();
    
    public boolean isSudoUsed() {
        return RANDOM.nextBoolean();
    }
}
