package com.djccnt15.study_springbatch.batch.jobparameter;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.enums.DifficultyEnum;
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

@Slf4j
@Batch
public class EnumJobParamBatchConfig {
    
    @Bean
    public Job enumJobParamJob(JobRepository jobRepository, Step enumJobParamStep) {
        return new JobBuilder("enumJobParamJob", jobRepository)
            .start(enumJobParamStep)
            .build();
    }
    
    @Bean
    public Step enumJobParamStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet enumJobParamTasklet
    ) {
        return new StepBuilder("enumJobParamStep", jobRepository)
            .tasklet(enumJobParamTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet enumJobParamTasklet(
        @Value("#{jobParameters['difficulty']}") DifficultyEnum difficulty
    ) {
        return (contribution, chunkContext) -> {
            log.info("difficulty input: {}", difficulty);
            var difficulty2int = switch (difficulty) {
                case EASY -> 1;
                case NORMAL -> 2;
                case HARD -> 3;
                case EXTREME -> 5;
            };
            log.info("difficulty level: {}", difficulty2int);
            return RepeatStatus.FINISHED;
        };
    }
}
