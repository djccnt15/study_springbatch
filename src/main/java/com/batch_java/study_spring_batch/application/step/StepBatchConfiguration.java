package com.batch_java.study_spring_batch.application.step;

import com.batch_java.study_spring_batch.batch.business.*;
import com.batch_java.study_spring_batch.batch.model.JobExecution;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepBatchConfiguration {
    
    @Bean
    public Job stepBatchJob(
        Step step1,
        Step step2,
        Step step3
    ) {
        return new StepJobBuilder()
            .start(step1)
            .next(step2)
            .next(step3)
            .listener(new JobExecutionListener() {
                @Override
                public void beforeJob(JobExecution jobExecution) {
                
                }
                
                @Override
                public void afterJob(JobExecution jobExecution) {
                
                }
                
                @Override
                public void afterJob(JobExecution jobExecution, String message) {
                
                }
            })
            .build();
    }
    
    @Bean
    public Step step1() {
        return new Step(() -> System.out.println("step1"));
    }
    
    @Bean
    public Step step2() {
        return new Step(() -> System.out.println("step2"));
    }
    
    @Bean
    public Step step3() {
        return new Step(() -> System.out.println("step3"));
    }
}
