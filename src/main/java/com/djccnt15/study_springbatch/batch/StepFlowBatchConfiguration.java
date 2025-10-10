package com.djccnt15.study_springbatch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
// @Batch
public class StepFlowBatchConfiguration {
    
    @Bean
    public Job flowJob(
        JobRepository jobRepository,
        Step step1,
        Step step2,
        Step step3,
        Step step4
    ) {
        return new JobBuilder("Flow-Job", jobRepository)
            .start(step1)
            .on("*").to(step2)
            .from(step1)
            .on("FAILED").to(step3)
            .from(step3)
            .on("COMPLETED").stopAndRestart(step4)
            .end().build();
    }
    
    @Bean
    public Step step1(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("step1", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 1");
                
                throw new IllegalStateException("step1 failed");
                
            }, platformTransactionManager)
            .build();
    }
    
    @Bean
    public Step step2(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("step2", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 2");
                return null;
            }, platformTransactionManager)
            .build();
    }
    
    @Bean
    public Step step3(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("step3", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 3");
                return null;
            }, platformTransactionManager)
            .build();
    }
    
    @Bean
    public Step step4(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("step4", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 4");
                return null;
            }, platformTransactionManager)
            .build();
    }
}
