package com.djccnt15.study_springbatch.batch.jobexecution;

import com.djccnt15.study_springbatch.annotation.Batch;
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
@Batch
public class JobScopeBatchConfig {
    
    @Bean
    public Job jobScopeJob(
        JobRepository jobRepository,
        Step jobScopeStep
    ) {
        return new JobBuilder("jobScopeJob", jobRepository)
            .start(jobScopeStep)
            .build();
    }
    
    // 지연된 빈 생성 (Lazy Bean Creation)
    // `@JobScope`가 적용된 빈은 애플리케이션 구동 시점에는 프록시 객체만 생성
    // 실제 인스턴스는 `Job`이 실행될 때 생성되어 `Job`이 종료하면 소멸됨(`Job`과 동일한 생명주기)
    // 이를 통해 애플리케이션 실행 중에 전달되는 `jobParameters`를 `Job` 실행 시점에 생성되는 빈의 실제 인스턴스에 주입 가능
    // API 등 외부 호출로 실행하는 애플리케이션의 경우 동적 파라미터를 `Step` 빈 인스턴스 생성 시에 주입 가능
    // 동시에 여러 요청이 같은 `Job` 정의를 서로 다른 파라미터로 실행하는 경우 `Tasklet` 인스턴스의 동시성 이슈 발생 가능
    // 프록시 객체와 실제 `Tasklet` 인스턴스를 구분해 `jobExecution`마다 다른 `Tasklet` 인스턴스를 생성해 해결 가능
    @Bean
    @JobScope  // `Step` 빈에 `@JobScope`를 선언하는 것 지양할 것
    public Step jobScopeStep(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager,
        @Value("#{jobParameters['jobScope']}") String jobScope
    ) {
        log.info("jobScope: {}", jobScope);
        return new StepBuilder("jobScopeStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                log.info("jobScopeStep 시작");
                return RepeatStatus.FINISHED;
            },
                platformTransactionManager
            )
            .build();
    }
}
