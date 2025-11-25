package com.djccnt15.study_springbatch.batch.flow;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class StepFlowBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    private final String FAILED = "FAILED";
    private final String COMPLETE = "COMPLETED";
    
    @Bean
    public Job stepFlowJob(
        Step stepFlowStep1,
        Step stepFlowStep2,
        Step stepFlowStep3,
        Step stepFlowStep4
    ) {
        return new JobBuilder("stepFlowJob", jobRepository)
            .start(stepFlowStep1)  // 시작
            // 와일드카드(*, ?)를 이용한 패턴 매칭 규칙으로 여러 ExitCode에 대한 규칙을 지정할 수 있음
            .on("*").to(stepFlowStep2)  // 이전 단계의 수행 결과에 따라 다음 단계 싷행 여부 지정
            // stepFlowStep2 와 같이 .on() -> .to()가 완성되지 않은 step들은 암시적 규칙 자동 생성
            // ExitCode가 COMPLETED인 경우: COMPLETED 상태의 EndState로 전이
            // ExitCode가 COMPLETED가 아닌 경우: FAILED 상태의 EndState로 전이
            .from(stepFlowStep1)  // 이전 단계의 수행 결과에 따른 작업 분기점을 설정
            .on(FAILED).to(stepFlowStep3)
            .from(stepFlowStep3)
            .on(COMPLETE).stopAndRestart(stepFlowStep4)
            .end().build();
    }
    
    @Bean
    public Step stepFlowStep1() {
        return new StepBuilder("stepFlowStep1", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 1");
                throw new IllegalStateException("step1 failed");
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step stepFlowStep2() {
        return new StepBuilder("stepFlowStep2", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 2");
                return null;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step stepFlowStep3() {
        return new StepBuilder("stepFlowStep3", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 3");
                return null;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step stepFlowStep4() {
        return new StepBuilder("stepFlowStep4", jobRepository)
            .tasklet((a, b) -> {
                log.info("execute step 4");
                return null;
            }, transactionManager)
            .build();
    }
}
