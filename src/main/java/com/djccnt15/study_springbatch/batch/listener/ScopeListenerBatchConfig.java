package com.djccnt15.study_springbatch.batch.listener;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
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
@Batch
public class ScopeListenerBatchConfig {
    
    @Bean
    public Job scopeListenerJob(
        JobRepository jobRepository,
        Step scopeListenerStep
    ) {
        return new JobBuilder("scopeListenerJob", jobRepository)
            .listener(scopeListener(null))  // 파라미터는 런타임에 주입
            .start(scopeListenerStep)
            .build();
    }
    
    @Bean
    public Step scopeListenerStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("scopeListenerStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("scopeListenerStep 실행");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    @JobScope  // JobExecutionListener는 JobExecution과 동일한 생명주기여야 함
    public JobExecutionListener scopeListener(
        @Value("#{jobParameters['scopeListenerParam']}") String scopeListenerParam
    ) {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("beforeJob - jobParameters: {}", scopeListenerParam);
            }
            
            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("afterJob - jobExecution: {}", jobExecution.getStatus());
            }
        };
    }
}
