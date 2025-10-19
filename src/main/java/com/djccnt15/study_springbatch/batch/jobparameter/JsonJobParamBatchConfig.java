package com.djccnt15.study_springbatch.batch.jobparameter;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.converter.JsonJobParametersConverter;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

// 환경에 따라 특수문자 escape 처리가 달라 사용하기 힘듦. 비추천
// jsonJobParam=\"{\\"value\\":\\"qwer,asdf\\",\\"type\\":\\"java.lang.String\\"}\"
@Slf4j
@Batch
@RequiredArgsConstructor
public class JsonJobParamBatchConfig {
    
    @Bean
    public JobParametersConverter jobParametersConverter() {
        return new JsonJobParametersConverter();
    }
    
    @Bean
    public Job jsonJobParamJob(JobRepository jobRepository, Step jsonJobParamStep) {
        return new JobBuilder("jsonJobParamJob", jobRepository)
            .start(jsonJobParamStep)
            .build();
    }
    
    @Bean
    public Step jsonJobParamStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet jsonJobParamTasklet
    ) {
        return new StepBuilder("jsonJobParamStep", jobRepository)
            .tasklet(jsonJobParamTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet jsonJobParamTasklet(
        @Value("#{jobParameters['jsonJobParam']}") String jsonJobParam
    ) {
        return (contribution, chunkContext) -> {
            String[] targets = jsonJobParam.split(",");
            
            log.info("targets[0]: {}", targets[0]);
            log.info("targets[1]: {}", targets[1]);
            
            return RepeatStatus.FINISHED;
        };
    }
}
