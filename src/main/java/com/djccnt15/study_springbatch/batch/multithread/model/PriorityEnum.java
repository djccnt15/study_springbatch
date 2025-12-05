package com.djccnt15.study_springbatch.batch.multithread.model;

public enum PriorityEnum {
    
    TERMINATE,
    HIGH,
    MONITOR,
    IGNORE
    ;
    
    public static PriorityEnum fromThreatScore(int threatScore) {
        if (threatScore >= 100) return TERMINATE;
        if (threatScore >= 50) return HIGH;
        if (threatScore >= 20) return MONITOR;
        return IGNORE;
    }
}
