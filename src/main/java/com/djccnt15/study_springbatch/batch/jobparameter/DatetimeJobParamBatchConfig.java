package com.djccnt15.study_springbatch.batch.jobparameter;

import com.djccnt15.study_springbatch.annotation.Batch;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Batch
public class DatetimeJobParamBatchConfig {
    
    @Bean
    public Job datetimeJobParamJob(JobRepository jobRepository, Step datetimeJobParamStep) {
        return new JobBuilder("datetimeJobParamJob", jobRepository)
            .start(datetimeJobParamStep)
            .build();
    }
    
    @Bean
    public Step datetimeJobParamStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        Tasklet datetimeJobParamTasklet
    ) {
        return new StepBuilder("datetimeJobParamStep", jobRepository)
            .tasklet(datetimeJobParamTasklet, transactionManager)
            .build();
    }
    
    
    // `LocalDate` 입력 시: `yyyy-MM-dd` 형식
    // `LocalDateTime` 입력 시: `yyyy-MM-ddThh:mm:ss` 형식
    @Bean
    @StepScope
    public Tasklet datetimeJobParamTasklet(
        @Value("#{jobParameters['executionDate']}") LocalDate executionDate,
        @Value("#{jobParameters['startTime']}") LocalDateTime startTime
    ) {
        return (contribution, chunkContext) -> {
            log.info("executionDate: {}", executionDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")));
            log.info("startTime: {}", startTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));
            
            var currentTime = startTime;
            for (int i = 1; i <= 3; i++) {
                currentTime = currentTime.plusHours(1);
                log.info("{}시간 경과... 현재 시각:{}", i, currentTime.format(DateTimeFormatter.ofPattern("HH시 mm분")));
            }
            log.info("종료 시각: {}", currentTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")));
            
            return RepeatStatus.FINISHED;
        };
    }
}
