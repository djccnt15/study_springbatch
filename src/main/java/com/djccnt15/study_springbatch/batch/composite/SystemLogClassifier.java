package com.djccnt15.study_springbatch.batch.composite;

import com.djccnt15.study_springbatch.batch.composite.model.SystemLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.classify.Classifier;

@Slf4j
@RequiredArgsConstructor
public class SystemLogClassifier implements Classifier<SystemLog, ItemWriter<? super SystemLog>> {
    
    public static final int CRITICAL_CPU_THRESHOLD = 90;
    public static final long CRITICAL_MEMORY_THRESHOLD = 1024 * 1024 * 1024; // 1GB
    
    private final ItemWriter<SystemLog> criticalWriter;
    private final ItemWriter<SystemLog> normalWriter;
    
    @Override
    public ItemWriter<SystemLog> classify(SystemLog systemLog) {
        if (isCritical(systemLog)) {
            return criticalWriter;
        }
        return normalWriter;
    }
    
    // 시스템의 생사를 가르는 판단 기준
    private boolean isCritical(SystemLog systemLog) {
        return "CRITICAL".equals(systemLog.getType()) ||
            systemLog.getCpuUsage() >= CRITICAL_CPU_THRESHOLD ||
            systemLog.getMemoryUsage() >= CRITICAL_MEMORY_THRESHOLD;
    }
}
