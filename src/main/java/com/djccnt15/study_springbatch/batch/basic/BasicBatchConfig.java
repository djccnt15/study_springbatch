package com.djccnt15.study_springbatch.batch.basic;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Batch
@RequiredArgsConstructor
public class BasicBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    private final AtomicInteger completedProcess = new AtomicInteger(0);
    private final int goal = 5;
    
    @Bean
    public Job basicBatchJob() {
        return new JobBuilder("basicBatchJob", jobRepository)
            .start(startStep())
            .next(secondStep())
            .next(thirdStep())
            .next(lastStep())
            .build();
    }
    
    @Bean
    public Step startStep() {
        return new StepBuilder("startStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("startStep 시작");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .allowStartIfComplete(true)  // JOB 재실행 시 COMPLETED 된 Step도 재실행 대상에 포함
            .startLimit(5)  // 배치 재시작으로 인한 Step의 재실행 가능 횟수 제한
            .build();
    }
    
    @Bean
    public Step secondStep() {
        return new StepBuilder("secondStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("secondStep: 작업 수행 목표 {}", goal);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step thirdStep() {
        return new StepBuilder("thirdStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                int terminated = completedProcess.incrementAndGet();
                log.info("thirdStep: 작업 수행 횟수 {}/{}", terminated, goal);
                if (terminated < goal) {
                    return RepeatStatus.CONTINUABLE;
                } else {
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step lastStep() {
        return new StepBuilder("lastStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("lastStep: {}개 수행 성공!", goal);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
