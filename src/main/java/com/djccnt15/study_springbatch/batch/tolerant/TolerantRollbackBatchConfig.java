package com.djccnt15.study_springbatch.batch.tolerant;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantRollbackBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job tolerantRollbackItemJob() {
        return new JobBuilder("tolerantRollbackItemJob", jobRepository)
            .start(tolerantRollbackItemStep())
            .build();
    }
    
    @Bean
    public Step tolerantRollbackItemStep() {
        return new StepBuilder("tolerantRollbackItemStep", jobRepository)
            .chunk(10, transactionManager)
            .reader(tolerantRollbackItemReader())
            // .processor()
            .writer(read -> {})
            .faultTolerant()
            .noRollback(IllegalStateException.class)
            .build();
    }
    
    @Bean
    public ItemReader<Integer> tolerantRollbackItemReader() {
        return new ItemReader<>() {
            private int count = 0;
            
            @Override
            public Integer read() {
                count++;
                
                log.info("Read {}", count);
                
                if (count == 20) {
                    return null;
                } else if (count >= 15) {
                    throw new IllegalStateException("Exception raised");
                }
                
                return count;
            }
        };
    }
}
