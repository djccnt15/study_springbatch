package com.djccnt15.study_springbatch.batch.jobexecution;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.jobexecution.validator.JobExecutionParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class JobExecutionParamBatchConfig {
    
    @Bean
    public Job jobExecutionJobDefaultValidator(
        JobRepository jobRepository,
        Step jobExecutionStep,
        JobExecutionParamValidator validator
    ) {
        return new JobBuilder("jobExecutionJobDefaultValidator", jobRepository)
            // 선택적 파라미터 배열에 값을 지정하면, validator에 지정되지 않은 파라미터는 입력 불가
            //   -> 입력되는 모든 파라미터는 반드시 필수/선택적 파라미터 중 하나에 포함되어야 함
            // 필수 파라미터의 존재 여부만 검증하고 다른 파라미터들은 자유롭게 전달하고 싶은 경우, 선택적 파라미터에는 빈 배열 입력
            .validator(new DefaultJobParametersValidator(
                new String[]{"jobParam.level"},  // 필수 파라미터
                new String[]{"target"}  // 선택적 파라미터
            ))
            .start(jobExecutionStep)
            .build();
    }
    
    @Bean
    public Job jobExecutionJob(
        JobRepository jobRepository,
        Step jobExecutionStep,
        JobExecutionParamValidator validator
    ) {
        return new JobBuilder("jobExecutionJob", jobRepository)
            .validator(validator)
            .start(jobExecutionStep)
            .build();
    }
    
    @Bean
    public Step jobExecutionStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet jobExecutionTasklet
    ) {
        return new StepBuilder("jobExecutionStep", jobRepository)
            .tasklet(jobExecutionTasklet, transactionManager)
            .build();
    }
}
