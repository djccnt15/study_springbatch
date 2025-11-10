package com.djccnt15.study_springbatch.batch.basic;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.basic.tasklet.DeleteOldFilesTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class FileCleanupBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job deleteOldFilesJob(Step deleteOldFilesStep) {
        return new JobBuilder("deleteOldFilesJob", jobRepository)
            .incrementer(new RunIdIncrementer())  // auto adds run.id
            .start(deleteOldFilesStep)
            .build();
    }
    
    @Bean
    public Step deleteOldFilesStep(Tasklet deleteOldFilesTasklet) {
        return new StepBuilder("deleteOldFilesStep", jobRepository)
            .tasklet(deleteOldFilesTasklet, transactionManager)
            .build();
    }
    
    @Bean
    @StepScope
    public Tasklet deleteOldFilesTasklet(
        @Value("#{jobParameters['targetPath']}") String targetPath,
        @Value("#{jobParameters['days']}") Integer days
    ) {
        return new DeleteOldFilesTasklet(targetPath, days);
    }
}
