package com.djccnt15.study_springbatch.batch.listener;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.listener.listener.v1.JobExecutionListenerV1;
import com.djccnt15.study_springbatch.batch.listener.listener.v1.StepExecutionListenerV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
public class ListenerBatchConfigV1 {
    
    @Bean
    public Job listenerJobV1(
        JobRepository jobRepository,
        Step listenerStepV1
    ) {
        return new JobBuilder("listenerJobV1", jobRepository)
            .listener(new JobExecutionListenerV1())
            .start(listenerStepV1)
            .build();
    }
    
    @Bean
    public Step listenerStepV1(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet listenerTasklet
    ) {
        return new StepBuilder("listenerStepV1", jobRepository)
            .listener(new StepExecutionListenerV1())
            .tasklet(listenerTasklet, transactionManager)
            .build();
    }
}
