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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
// @Batch
public class FlatFileBatchConfiguration {
    
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
        ItemReader<UserEntity> flatFileItemReader,
        ItemWriter<UserEntity> formattedFlatFileItemWriter
    ) {
        return new StepBuilder("step", jobRepository)
            .<UserEntity, UserEntity>chunk(2, transactionManager)
            .reader(flatFileItemReader)
            .writer(formattedFlatFileItemWriter)
            .build();
    }
    
    @Bean
    public FlatFileItemReader<UserEntity> flatFileItemReader() {
        return new FlatFileItemReaderBuilder<UserEntity>()
            .name("flatFileItemReader")
            .resource(new ClassPathResource("users.txt"))
            .linesToSkip(1)
            .delimited().delimiter(",")
            .names("name", "age", "region", "phoneNumber")
            .targetType(UserEntity.class)
            .strict(true)
            .build();
    }
    
    @Bean
    public ItemWriter<UserEntity> flatFileItemWriter() {
        return new FlatFileItemWriterBuilder<UserEntity>()
            .name("flatFileItemWriter")
            .resource(new PathResource("src/main/resources/new_users.txt"))
            .delimited().delimiter(",")
            .names("name", "age", "region", "phoneNumber")
            .shouldDeleteIfEmpty(true)
            .shouldDeleteIfExists(true)
            .append(true)
            .build();
    }
    
    @Bean
    public ItemWriter<UserEntity> formattedFlatFileItemWriter() {
        return new FlatFileItemWriterBuilder<UserEntity>()
            .name("flatFileItemWriter")
            .resource(new PathResource("src/main/resources/new_users.txt"))
            .formatted().format("name: %s, age: %s, region: %s, phoneNumber: %s")
            .names("name", "age", "region", "phoneNumber")
            .shouldDeleteIfEmpty(true)
            .shouldDeleteIfExists(true)
            .append(true)
            .build();
    }
}
