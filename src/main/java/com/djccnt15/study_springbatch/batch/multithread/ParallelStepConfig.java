package com.djccnt15.study_springbatch.batch.multithread;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class ParallelStepConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job parallelStepJob(
        Step step4,
        Flow splitFlow
    ) {
        return new JobBuilder("parallelStepJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(splitFlow)
            .next(step4)
            .build().build();
    }
    
    @Bean
    public Flow splitFlow(Flow flow1, Flow flow2) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
            .split(new SimpleAsyncTaskExecutor())
            .add(flow1, flow2)
            .build();
    }
    
    @Bean
    public Flow flow1(Step step1, Step step2) {
        return new FlowBuilder<SimpleFlow>("flow1")
            .start(step1)
            .next(step2)
            .build();
    }
    
    @Bean
    public Flow flow2(Step step3) {
        return new FlowBuilder<SimpleFlow>("flow1")
            .start(step3)
            .build();
    }
    
    @Bean
    public Step step1() {
        return new StepBuilder("step1", jobRepository)
            .tasklet((a, b) -> {
                Thread.sleep(1000);
                log.info("step1");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step step2() {
        return new StepBuilder("step2", jobRepository)
            .tasklet((a, b) -> {
                Thread.sleep(2000);
                log.info("step2");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step step3() {
        return new StepBuilder("step3", jobRepository)
            .tasklet((a, b) -> {
                Thread.sleep(2500);
                log.info("step3");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
    
    @Bean
    public Step step4() {
        return new StepBuilder("step4", jobRepository)
            .tasklet((a, b) -> {
                Thread.sleep(1000);
                log.info("step4");
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }
}
