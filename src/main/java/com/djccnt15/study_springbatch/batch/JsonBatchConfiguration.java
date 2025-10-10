package com.djccnt15.study_springbatch.batch;

import com.djccnt15.study_springbatch.model.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
// @Batch
public class JsonBatchConfiguration {

    @Bean
    public Job job(
        JobRepository jobRepository,
        Step step
    ) {
        return new JobBuilder("itemReaderJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(step)
            .build();
    }
    
    @Bean
    public Step step(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<UserEntity> jsonItemReader,
        ItemWriter<UserEntity> jsonFileItemWriter
    ) {
        return new StepBuilder("step", jobRepository)
            .<UserEntity, UserEntity>chunk(2, transactionManager)
            .reader(jsonItemReader)
            .writer(jsonFileItemWriter)
            .build();
    }
    
    @Bean
    public JsonItemReader<UserEntity> jsonItemReader() {
        return new JsonItemReaderBuilder<UserEntity>()
            .name("jsonItemReader")
            .resource(new ClassPathResource("users.json"))
            .jsonObjectReader(new JacksonJsonObjectReader<>(UserEntity.class))
            .build();
    }
    
    @Bean
    public JsonFileItemWriter<UserEntity> jsonFileItemWriter() {
        return new JsonFileItemWriterBuilder<UserEntity>()
            .name("jsonFileItemWriter")
            .resource(new PathResource("src/main/resources/new_user.json"))
            .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
            .build();
    }
}
