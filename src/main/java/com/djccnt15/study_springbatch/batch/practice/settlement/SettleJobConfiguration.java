package com.djccnt15.study_springbatch.batch.practice.settlement;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.practice.settlement.utils.DateFormatJobParamValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
            var formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            var targetDate = stepExecution.getJobParameters().getString("targetDate");
            var date = LocalDate.parse(targetDate, formatter);
            
            if (date.getDayOfWeek() != DayOfWeek.TUESDAY) {
                return new FlowExecutionStatus("NoOps");
            }
            
            return FlowExecutionStatus.COMPLETED;
        };
    }
}
