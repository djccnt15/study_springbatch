package com.batch_java.study_spring_batch.settlement_batch;

import com.batch_java.study_spring_batch.common.Batch;
import com.batch_java.study_spring_batch.settlement_batch.utils.DateFormatJobParamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;

// @Batch
@RequiredArgsConstructor
public class SettleJobConfiguration {

    private final JobRepository jobRepository;
    
    @Bean
    public Job settleJob(
        Step preSettleDetailStep,
        Step settleDetailStep
    ) {
        return new JobBuilder("settleJob", jobRepository)
            .validator(new DateFormatJobParamValidator(new String[]{"targetDate"}))
            .start(preSettleDetailStep)
            .next(settleDetailStep)
            .build();
    }
}
