package com.djccnt15.study_springbatch.batch.practice.log;

public enum LogLevelEnum {

    INFO,
    WARN,
    ERROR,
    DEBUG,
    UNKNOWN;
    
    public static LogLevelEnum fromString(String level) {
        if (level == null || level.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
