package com.djccnt15.study_springbatch.batch.flatfile.json;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.json.model.SystemFailureRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class JsonlArrayReaderBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job jsonlArrayReaderJob(Step jsonlArrayReaderStep) {
        return new JobBuilder("jsonlArrayReaderJob", jobRepository)
            .start(jsonlArrayReaderStep)
            .build();
    }
    
    @Bean
    public Step jsonlArrayReaderStep(
        JsonItemReader<SystemFailureRecord> jsonlArrayReader
    ) {
        return new StepBuilder("jsonlArrayReaderStep", jobRepository)
            .<SystemFailureRecord, SystemFailureRecord>chunk(10, transactionManager)
            .reader(jsonlArrayReader)
            .writer(items -> items.forEach(System.out::println))
            .build();
    }
    
    @Bean
    @StepScope
    public JsonItemReader<SystemFailureRecord> jsonlArrayReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new JsonItemReaderBuilder<SystemFailureRecord>()
            .name("jsonlArrayReader")
            .jsonObjectReader(new JacksonJsonObjectReader<>(SystemFailureRecord.class))
            .resource(new FileSystemResource(inputFile))
            .build();
    }
}
