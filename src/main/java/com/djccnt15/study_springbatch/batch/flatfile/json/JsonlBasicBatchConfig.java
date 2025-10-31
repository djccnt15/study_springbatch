package com.djccnt15.study_springbatch.batch.flatfile.json;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.json.model.SystemFailureRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.separator.JsonRecordSeparatorPolicy;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class JsonlBasicBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;
    
    @Bean
    public Job jsonlBasicBatchJob(Step jsonlBasicBatchStep) {
        return new JobBuilder("jsonlBasicBatchJob", jobRepository)
            .start(jsonlBasicBatchStep)
            .build();
    }
    
    @Bean
    public Step jsonlBasicBatchStep(
        FlatFileItemReader<SystemFailureRecord> jsonlBasicReader,
        JsonFileItemWriter<SystemFailureRecord> jsonlBasicWriter
    ) {
        return new StepBuilder("jsonlBasicBatchStep", jobRepository)
            .<SystemFailureRecord, SystemFailureRecord>chunk(10, transactionManager)
            .reader(jsonlBasicReader)
            .writer(jsonlBasicWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<SystemFailureRecord> jsonlBasicReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new FlatFileItemReaderBuilder<SystemFailureRecord>()
            .name("jsonlBasicReader")
            .resource(new FileSystemResource(inputFile))
            .lineMapper((line, lineNumber) -> objectMapper
                .readValue(line, SystemFailureRecord.class))
            .recordSeparatorPolicy(new JsonRecordSeparatorPolicy())
            .build();
    }
    
    @Bean
    @StepScope
    public JsonFileItemWriter<SystemFailureRecord> jsonlBasicWriter(
        @Value("#{jobParameters['outputDir']}") String outputDir
    ) {
        return new JsonFileItemWriterBuilder<SystemFailureRecord>()
            .name("jsonlBasicWriter")
            .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
            .resource(new FileSystemResource(outputDir + "/system_failure.json"))
            .build();
    }
}
