package com.djccnt15.study_springbatch.batch.jobparameter;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
public class BasicJobParameterBatchConfig {
    
    @Bean
    public Job basicJobParamJob(
        JobRepository jobRepository,
        Step basicJobParamStep
    ) {
        return new JobBuilder("basicJobParamJob", jobRepository)
            .start(basicJobParamStep)
            .build();
    }
    
    @Bean
    public Step basicJobParamStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet basicJobParamTasklet
    ) {
        return new StepBuilder("basicJobParamStep", jobRepository)
            .tasklet(basicJobParamTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet basicJobParamTasklet(
        @Value("#{jobParameters['authorId']}") String authorId,
        @Value("#{jobParameters['targetCount']}") Integer targetCount
    ) {
        return (contribution, chunkContext) -> {
            log.info("authorId: {}", authorId);
            log.info("targetCount: {}", targetCount);
            
            for (int i = 1; i <= targetCount; i++) {
                log.info("태스크 {} 수행", i);
            }
            return RepeatStatus.FINISHED;
        };
    }
}
