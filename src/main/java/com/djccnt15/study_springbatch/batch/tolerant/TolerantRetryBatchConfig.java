package com.djccnt15.study_springbatch.batch.tolerant;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantRetryBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job tolerantRetryJob() {
        return new JobBuilder("tolerantRetryJob", jobRepository)
            .start(tolerantRetryStep())
            .build();
    }
    
    @Bean
    public Step tolerantRetryStep() {
        return new StepBuilder("tolerantRetryStep", jobRepository)
            .<Integer, Integer>chunk(10, transactionManager)
            .reader(tolerantRetryItemReader())
            .processor(tolerantRetryItemProcessor())
            .writer(read -> {})
            .faultTolerant()
            .retry(IllegalStateException.class)
            .retryLimit(3)
            .build();
    }
    
    @Bean
    public ItemReader<Integer> tolerantRetryItemReader() {
        return new ItemReader<>() {
            int count = 0;
            
            @Override
            public Integer read() {
                count++;
                
                log.info("Read {}", count);
                
                if (count == 20) {
                    return null;
                }
                
                return count;
            }
        };
    }
    
    @Bean
    public ItemProcessor<Integer, Integer> tolerantRetryItemProcessor() {
        return item -> {
            if (item == 15) {
                throw new IllegalStateException();
            }
            return item;
        };
    }
}
