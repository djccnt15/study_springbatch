package com.djccnt15.study_springbatch.batch.jobexecution;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.jobexecution.tasklet.StepScopeTasklet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
public class StepScopeBatchConfigT1 {
    
    @Bean
    public Job stepScopeJobT1(
        JobRepository jobRepository,
        Step stepScopeStepT1
    ) {
        return new JobBuilder("stepScopeJobT1", jobRepository)
            .start(stepScopeStepT1)
            .build();
    }
    
    @Bean
    public Step stepScopeStepT1(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet stepScopeTaskletT1
    ) {
        return new StepBuilder("stepScopeStepT1", jobRepository)
            .tasklet(stepScopeTaskletT1, transactionManager)
            .build();
    }
    
    // `@StepScope`는 `Step`의 실행 범위에서 빈을 관리(적용 빈은 `Step`과 동일한 생명주기)
    // 동시에 여러 `Step`이 실행되면서 빈 객체를 사용해도, `@StepScope`가 있어 각 `Step` 실행마다 독립적인 `Tasklet` 인스턴스 생성
    // `Tasklet` 객체 재활용 시 발생할 수 있는 동시성 이슈 방지 가능
    @Bean
    @StepScope
    public Tasklet stepScopeTaskletT1(
        @Value("#{jobParameters['stepScope']}") String stepScope
    ) {
        return new StepScopeTasklet(stepScope);
    }
}
