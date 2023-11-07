package com.batch_java.study_spring_batch.application.config;

import com.batch_java.study_spring_batch.application.business.DormantBatchItemProcessor;
import com.batch_java.study_spring_batch.application.business.DormantBatchItemReader;
import com.batch_java.study_spring_batch.application.business.DormantBatchItemWriter;
import com.batch_java.study_spring_batch.application.business.DormantBatchJobExecutionListener;
import com.batch_java.study_spring_batch.batch.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DormantBatchConfiguration {

    @Bean
    public Job dormantBatchJob(
        DormantBatchItemReader itemReader,
        DormantBatchItemProcessor itemProcessor,
        DormantBatchItemWriter itemWriter,
        DormantBatchJobExecutionListener listener
    ) {
        return Job.builder()
            .itemReader(itemReader)
            .itemProcessor(itemProcessor)
            .itemWriter(itemWriter)
            .jobExecutionListener(listener)
            .build();
    }
}
