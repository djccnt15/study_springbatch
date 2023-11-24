package com.batch_java.study_spring_batch.batch;

import com.batch_java.study_spring_batch.common.Batch;
import com.batch_java.study_spring_batch.model.User;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
public class DatabaseItemReaderBatchConfiguration {

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
        ItemReader<User> jpaCursorItemReader
    ) {
        return new StepBuilder("step", jobRepository)
            .<User, User>chunk(2, transactionManager)
            .reader(jpaCursorItemReader)
            .writer(System.out::println)
            .build();
    }
    
    @Bean
    public ItemReader<User> jpaPagingItemReader(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaPagingItemReaderBuilder<User>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(3)
            .queryString("SELECT u FROM User u ORDER BY u.id")  // 실제 테이블 명 x, JPA 클래스 이름 사용
            .build();
    }
    
    @Bean
    public ItemReader<User> jpaCursorItemReader(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaCursorItemReaderBuilder<User>()
            .name("JpaCursorItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT u FROM User u ORDER BY u.id")
            .build();
    }
}
