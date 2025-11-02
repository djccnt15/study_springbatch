package com.djccnt15.study_springbatch.batch.basic;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
public class HelloSpringBatchConfig {
    
    @Bean
    public Job helloWorldJob(
        JobRepository jobRepository,
        Step helloWorldStep
    ){
        return new JobBuilder("helloWorldJob", jobRepository)
            .start(helloWorldStep)
            .build();
    }
    
    @Bean
    public Step helloWorldStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("helloWorldStep", jobRepository)
            .tasklet(helloWorldTasklet(), transactionManager)
            .build();
    }
    
    public Tasklet helloWorldTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("=== HELLO SPRING BATCH ===");
            return RepeatStatus.FINISHED;
        };
    }
}
