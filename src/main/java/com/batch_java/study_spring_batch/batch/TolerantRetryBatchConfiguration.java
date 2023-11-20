package com.batch_java.study_spring_batch.batch;

import com.batch_java.study_spring_batch.common.Batch;
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
// @Batch
public class TolerantRetryBatchConfiguration {

    @Bean
    public Job job(
        JobRepository jobRepository,
        Step step
    ) {
        return new JobBuilder("Job-tolerant-rollback", jobRepository)
            .start(step)
            .build();
    }
    
    @Bean
    public Step step(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager
    ) {
        ItemReader<Integer> itemReader = new ItemReader<>() {
            private int count = 0;
            
            @Override
            public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                count++;
                
                log.info("Read {}", count);
                
                if (count == 20) {
                    return null;
                }
                
                return count;
            }
        };
        
        ItemProcessor<Integer, Integer> itemProcessor = new ItemProcessor<>() {
            
            @Override
            public Integer process(Integer item) throws Exception {
                if (item == 15) {
                    throw new IllegalStateException();
                }
                return item;
            }
        };
        
        return new StepBuilder("step", jobRepository)
            .<Integer, Integer>chunk(10, platformTransactionManager)
            .reader(itemReader)
            .processor(itemProcessor)
            .writer(read -> {})
            .faultTolerant()
            .retry(IllegalStateException.class)
            .retryLimit(3)
            .build();
    }
}
