package com.batch_java.study_spring_batch.settlement_batch;

import com.batch_java.study_spring_batch.common.Batch;
import com.batch_java.study_spring_batch.settlement_batch.utils.DateFormatJobParamValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Batch
@RequiredArgsConstructor
public class SettleJobConfiguration {
    
    private final JobRepository jobRepository;
    
    @Bean
    public Job settleJob(
        Step preSettleDetailStep,
        Step settleDetailStep,
        Step settleGroupStep
    ) {
        return new JobBuilder("settleJob", jobRepository)
            .validator(new DateFormatJobParamValidator(new String[]{"targetDate"}))
            .start(preSettleDetailStep)
            .next(settleDetailStep)
            .next(isFridayDecider())
            .on("COMPLETED").to(settleGroupStep)
            .build()
            .build();
    }
    
    @Bean
    public JobExecutionDecider isFridayDecider() {
        return (jobExecution, stepExecution) -> {
            final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            final String targetDate = stepExecution.getJobParameters().getString("targetDate");
            final LocalDate date = LocalDate.parse(targetDate, formatter);
            
            if (date.getDayOfWeek() != DayOfWeek.TUESDAY) {
                return new FlowExecutionStatus("NoOps");
            }
            
            return FlowExecutionStatus.COMPLETED;
        };
    }
}
