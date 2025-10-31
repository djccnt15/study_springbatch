package com.djccnt15.study_springbatch.batch.flatfile.json;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.model.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
public class JsonFileBatchConfig {

    @Bean
    public Job jsonFileBatchJob(
        JobRepository jobRepository,
        Step jsonFileStep
    ) {
        return new JobBuilder("jsonFileBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(jsonFileStep)
            .build();
    }
    
    @Bean
    public Step jsonFileBatchStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<UserEntity> jsonFileItemReader,
        ItemWriter<UserEntity> jsonFileItemWriter
    ) {
        return new StepBuilder("jsonFileBatchStep", jobRepository)
            .<UserEntity, UserEntity>chunk(2, transactionManager)
            .reader(jsonFileItemReader)
            .writer(jsonFileItemWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public JsonItemReader<UserEntity> jsonFileItemReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new JsonItemReaderBuilder<UserEntity>()
            .name("jsonFileItemReader")
            .resource(new FileSystemResource(inputFile))
            .jsonObjectReader(new JacksonJsonObjectReader<>(UserEntity.class))
            .build();
    }
    
    @Bean
    @StepScope
    public JsonFileItemWriter<UserEntity> jsonFileItemWriter(
        @Value("#{jobParameters['outputDir']}") String outputDir
    ) {
        return new JsonFileItemWriterBuilder<UserEntity>()
            .name("jsonFileItemWriter")
            .resource(new FileSystemResource(outputDir + "/new_user.json"))
            .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
            .build();
    }
}
