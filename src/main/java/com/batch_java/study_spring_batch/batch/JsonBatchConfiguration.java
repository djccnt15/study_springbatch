package com.batch_java.study_spring_batch.batch;

import com.batch_java.study_spring_batch.common.Batch;
import com.batch_java.study_spring_batch.model.User;
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
        ItemReader<User> jsonItemReader,
        ItemWriter<User> jsonFileItemWriter
    ) {
        return new StepBuilder("step", jobRepository)
            .<User, User>chunk(2, transactionManager)
            .reader(jsonItemReader)
            .writer(jsonFileItemWriter)
            .build();
    }
    
    @Bean
    public JsonItemReader<User> jsonItemReader() {
        return new JsonItemReaderBuilder<User>()
            .name("jsonItemReader")
            .resource(new ClassPathResource("users.json"))
            .jsonObjectReader(new JacksonJsonObjectReader<>(User.class))
            .build();
    }
    
    @Bean
    public JsonFileItemWriter<User> jsonFileItemWriter() {
        return new JsonFileItemWriterBuilder<User>()
            .name("jsonFileItemWriter")
            .resource(new PathResource("src/main/resources/new_user.json"))
            .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
            .build();
    }
}
