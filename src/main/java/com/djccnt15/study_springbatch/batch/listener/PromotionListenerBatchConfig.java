package com.djccnt15.study_springbatch.batch.listener;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import static com.djccnt15.study_springbatch.batch.listener.ListenerString.TARGET_SYSTEM;

// Step 수준의 ExecutionContext 데이터를 Job 수준의 ExecutionContext로 승격(Promote)
@Slf4j
@Batch
public class PromotionListenerBatchConfig {
    
    @Bean
    public Job promotionListenerJob(
        JobRepository jobRepository,
        Step promotionListenerFirstStep,
        Step promotionListenerSecondStep
    ) {
        return new JobBuilder("promotionListenerJob", jobRepository)
            .start(promotionListenerFirstStep)
            .next(promotionListenerSecondStep)
            .build();
    }
    
    @Bean
    public Step promotionListenerFirstStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("promotionListenerFirstStep", jobRepository)
            .listener(promotionListener())
            .tasklet((contribution, chunkContext) -> {
                var target = "판교 서버실";
                var stepContext = contribution.getStepExecution().getExecutionContext();
                stepContext.put(TARGET_SYSTEM, target);  // StepExecution에 데이터 입력
                log.info("타겟 스캔 완료: {}", target);
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step promotionListenerSecondStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet promotionListenerTasklet
    ) {
        return new StepBuilder("promotionListenerSecondStep", jobRepository)
            .tasklet(promotionListenerTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet promotionListenerTasklet(
        @Value("#{jobExecutionContext['targetSystem']}") String target
    ) {
        return (contribution, chunkContext) -> {
            // jobExecutionContext에서 데이터 읽기
            log.info("시스템 제거 작업 실행: {}", target);
            return RepeatStatus.FINISHED;
        };
    }
    
    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        var listener = new ExecutionContextPromotionListener();
        // StepExecution에서 jobExecutionContext로 승격시킬 대상 지정
        listener.setKeys(new String[]{TARGET_SYSTEM});
        return listener;
    }
}
