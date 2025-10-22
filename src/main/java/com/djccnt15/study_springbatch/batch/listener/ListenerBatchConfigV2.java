package com.djccnt15.study_springbatch.batch.listener;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.listener.listener.v2.JobExecutionListenerV2;
import com.djccnt15.study_springbatch.batch.listener.listener.v2.StepExecutionListenerV2;
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
public class ListenerBatchConfigV2 {
    
    @Bean
    public Job listenerJobV2(
        JobRepository jobRepository,
        Step listenerStepV2
    ) {
        return new JobBuilder("listenerJobV2", jobRepository)
            .listener(new JobExecutionListenerV2())
            .start(listenerStepV2)
            .build();
    }
    
    @Bean
    public Step listenerStepV2(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet listenerTasklet
    ) {
        return new StepBuilder("listenerStepV2", jobRepository)
            .listener(new StepExecutionListenerV2())
            .tasklet(listenerTasklet, transactionManager)
            .build();
    }
}
