package com.djccnt15.study_springbatch.batch.practice.settlement.sample_data_generator;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@StepScope
public class ApiOrderGenerateReader implements ItemReader<String> {
    
    private final Long totalCount;
    private final AtomicLong current;
    private final String targetDate;
    
    public ApiOrderGenerateReader(
        @Value("#{jobParameters['totalCount']}") String totalCount,
        @Value("#{stepExecutionContext['targetDate']}") String targetDate
    ) {
        this.totalCount = Long.parseLong(totalCount);
        this.current = new AtomicLong(0);
        this.targetDate = targetDate;
    }
    
    @Override
    public String read() throws Exception {
        if (current.incrementAndGet() > totalCount) {
            return null;
        }
        return targetDate;
    }
}
