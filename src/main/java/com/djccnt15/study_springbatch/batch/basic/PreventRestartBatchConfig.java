package com.djccnt15.study_springbatch.batch.basic;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class PreventRestartBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job preventRestartJob(){
        return new JobBuilder("preventRestartJob", jobRepository)
            .incrementer(new RunIdIncrementer())  // auto adds run.id
            .start(preventRestartStep())
            // RunIdIncrementer를 적용하면 run.id 값이 변경된 새로운 JobInstance를 생성하여, preventRestart() 설정 무력화
            .preventRestart()  // Job 실행이 실패하더라도 재실행 금지
            .build();
    }
    
    @Bean
    public Step preventRestartStep() {
        return new StepBuilder("preventRestartStep", jobRepository)
            .tasklet(preventRestartTasklet(), transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet preventRestartTasklet() {
        return (contribution, chunkContext) -> {
            System.out.println("=== HELLO SPRING BATCH ===");
            return RepeatStatus.FINISHED;
        };
    }
}
