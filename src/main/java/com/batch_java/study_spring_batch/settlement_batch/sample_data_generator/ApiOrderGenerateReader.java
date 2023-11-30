package com.batch_java.study_spring_batch.settlement_batch.sample_data_generator;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
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
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (current.incrementAndGet() > totalCount) {
            return null;
        }
        return targetDate;
    }
}
