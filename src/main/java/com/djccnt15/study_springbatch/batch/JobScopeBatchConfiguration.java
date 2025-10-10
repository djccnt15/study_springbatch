package com.djccnt15.study_springbatch.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
// @Batch
public class JobScopeBatchConfiguration {

    @Bean
    public Job job(
        JobRepository jobRepository,
        Step step
    ) {
        return new JobBuilder("Job-Scope", jobRepository)
            .start(step)
            .build();
    }
    
    @Bean
    @JobScope
    public Step step(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager,
        @Value("#{jobParameters['name']}") String name
    ) {
        log.info("name: {}", name);
        return new StepBuilder("step", jobRepository)
            .tasklet(
                (a, b) -> RepeatStatus.FINISHED,
                platformTransactionManager
            )
            .build();
    }
}
