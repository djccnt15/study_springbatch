package com.djccnt15.study_springbatch.batch;

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
// @Batch
public class ChunkBatchConfiguration {

    @Bean
    public Job job(
        JobRepository jobRepository,
        Step step
    ) {
        return new JobBuilder("Job-chunk", jobRepository)
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
            public Integer read() throws Exception {
                count++;
                
                log.info("Read {}", count);
                
                if (count == 15) {
                    return null;
                }
                
                return count;
            }
        };
        
        return new StepBuilder("step", jobRepository)
            .chunk(10, platformTransactionManager)
            .reader(itemReader)
            // .processor()
            .writer(read -> {})
            .build();
    }
}
