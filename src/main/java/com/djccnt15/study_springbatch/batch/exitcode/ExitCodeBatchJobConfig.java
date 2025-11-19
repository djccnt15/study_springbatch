package com.djccnt15.study_springbatch.batch.exitcode;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import static com.djccnt15.study_springbatch.batch.exitcode.ExitCodeMap.*;

@Slf4j
@Batch
@RequiredArgsConstructor
public class ExitCodeBatchJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final MyExitCodeGenerator myExitCodeGenerator;
    
    @Bean
    public Job exitCodeBatchJob(Step exitCodeBatchStep) {
        return new JobBuilder("exitCodeBatchJob", jobRepository)
            .start(exitCodeBatchStep)
            .listener(myExitCodeGenerator)
            .build();
    }
    
    @Bean
    public Step exitCodeBatchStep(Tasklet exitCodeTasklet) {
        return new StepBuilder("exitCodeBatchStep", jobRepository)
            .tasklet(exitCodeTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet exitCodeTasklet(
        @Value("#{jobParameters['error']}") Boolean error,
        @Value("#{jobParameters['reason']}") String reason) {
        
        return (contribution, chunkContext) -> {
            try {
                log.info("processing...");
                if (error) {
                    switch (reason) {
                        case ERROR_CASE_1:
                            throw new IllegalStateException("IllegalStateException case..");
                        case ERROR_CASE_2:
                            throw new ValidationException("ValidationException case..");
                        default:
                            throw new IllegalArgumentException("IllegalArgumentException case..");
                    }
                }
                log.info("processing...");
                return RepeatStatus.FINISHED;
            } catch (IllegalStateException e) {
                contribution.setExitStatus(new ExitStatus(ERROR_CASE_1, e.getMessage()));
                throw e;
            } catch (ValidationException e) {
                contribution.setExitStatus(new ExitStatus(ERROR_CASE_2, e.getMessage()));
                throw e;
            } catch (IllegalArgumentException e) {
                contribution.setExitStatus(new ExitStatus(ERROR_CASE_3, e.getMessage()));
                throw e;
            }
        };
    }
}
