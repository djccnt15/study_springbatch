package com.djccnt15.study_springbatch.batch.jobexecution;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
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
public class ExecutionContextBatchConfig {
    
    @Bean
    public Job jobExecutionContextJob(
        JobRepository jobRepository,
        Step jobExecutionContextStep,
        Step stepExecutionContextStep
    ) {
        return new JobBuilder("jobExecutionContextJob", jobRepository)
            .start(jobExecutionContextStep)
            .next(stepExecutionContextStep)
            .build();
    }
    
    @Bean
    public Step jobExecutionContextStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet jobExecutionContextTasklet
    ) {
        return new StepBuilder("jobExecutionContextStep", jobRepository)
            .tasklet(jobExecutionContextTasklet, transactionManager)
            .build();
    }
    
    @Bean
    public Step stepExecutionContextStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet stepExecutionContextTasklet
    ) {
        return new StepBuilder("stepExecutionContextStep", jobRepository)
            .tasklet(stepExecutionContextTasklet, transactionManager)
            .build();
    }
    
    // Job의 ExecutionContext는 Job에 속한 모든 컴포넌트에서 접근 가능
    @Bean
    @JobScope
    public Tasklet jobExecutionContextTasklet(
        @Value("#{jobExecutionContext['previousSystemState']}") String prevState
    ) {
        // JobExecution의 ExecutionContext에서 이전 시스템 상태를 주입 받음
        return (contribution, chunkContext) -> {
            log.info("prevState: {}", prevState);
            return RepeatStatus.FINISHED;
        };
    }
    
    // Step의 ExecutionContext는 오직 해당 Step에 속한 컴포넌트에서만 접근 가능
    @Bean
    @StepScope
    public Tasklet stepExecutionContextTasklet(
        @Value("#{stepExecutionContext['targetSystemStatus']}") String targetStatus
    ) {
        // StepExecution의 ExecutionContext에서 타겟 시스템 상태를 주입 받음
        return (contribution, chunkContext) -> {
            log.info("targetStatus: {}", targetStatus);
            return RepeatStatus.FINISHED;
        };
    }
}
