package com.djccnt15.study_springbatch.batch.tolerant.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Scream {
    
    private int id;
    private String scream;
    private String processMsg;
    
    @Override
    public String toString() {
        return id + "_" + scream;
    }
}
