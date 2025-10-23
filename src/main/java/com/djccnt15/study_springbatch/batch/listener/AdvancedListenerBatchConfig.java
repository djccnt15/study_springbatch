package com.djccnt15.study_springbatch.batch.listener;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
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

import java.util.Map;
import java.util.Random;

import static com.djccnt15.study_springbatch.batch.listener.ListenerString.*;

@Slf4j
@Batch
@RequiredArgsConstructor
public class AdvancedListenerBatchConfig {
    
    private final AdvancedListener advancedListener;
    
    @Bean
    public Job advancedListenerJob(
        JobRepository jobRepository,
        Step advancedListenerFirstStep,
        Step advancedListenerSecondStep
    ) {
        return new JobBuilder("advancedListenerJob", jobRepository)
            .listener(advancedListener)
            .start(advancedListenerFirstStep)
            .next(advancedListenerSecondStep)
            .build();
    }
    
    @Bean
    public Step advancedListenerFirstStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("advancedListenerFirstStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                @SuppressWarnings("unchecked")
                var jobExecutePlan = (Map<String, Object>) chunkContext.getStepContext()
                    .getJobExecutionContext().get(JOB_EXECUTE_PLAN);
                log.info("firstStep - TARGET_SYSTEM: {}, REQUIRED_TOOLS: {}",
                    jobExecutePlan.get(TARGET_SYSTEM), jobExecutePlan.get(REQUIRED_TOOLS));
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step advancedListenerSecondStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet advancedListenerTasklet
    ) {
        return new StepBuilder("advancedListenerSecondStep", jobRepository)
            .tasklet(advancedListenerTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet advancedListenerTasklet(
        @Value("#{jobExecutionContext['jobExecutePlan']}") Map<String, Object> jobExecutePlan
    ) {
        return (contribution, chunkContext) -> {
            log.info("tasklet - TARGET_SYSTEM: {}, OBJECTIVE: {}",
                jobExecutePlan.get(TARGET_SYSTEM), jobExecutePlan.get(OBJECTIVE));
            
            if (new Random().nextBoolean()) {
                log.info("success, TARGET_DATA: {}", jobExecutePlan.get(TARGET_DATA));
                contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .put(JOB_RESULT, TERMINATED);
            } else {
                log.info("fail");
                contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .put(JOB_RESULT, DETECTED);
            }
            return RepeatStatus.FINISHED;
        };
    }
}
