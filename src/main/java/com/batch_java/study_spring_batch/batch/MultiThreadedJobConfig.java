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
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
// @Batch
public class MultiThreadedJobConfig {
    
    @Bean
    public Job job(
        JobRepository jobRepository,
        Step step
    ) {
        return new JobBuilder("multiThreadedJob", jobRepository)
            .start(step)
            .incrementer(new RunIdIncrementer())
            .build();
    }
    
    @Bean
    public Step step(
        JobRepository jobRepository,
        PlatformTransactionManager platformTransactionManager,
        JpaPagingItemReader<User> jpaPagingItemReader
    ) {
        return new StepBuilder("step", jobRepository)
            .<User, User>chunk(2, platformTransactionManager)
            .reader(jpaPagingItemReader)
            .writer(result -> log.info(result.toString()))
            .taskExecutor(new SimpleAsyncTaskExecutor())
            .build();
    }
    
    @Bean
    public JpaPagingItemReader<User> jobPagingItemReader(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaPagingItemReaderBuilder<User>()
            .name("jobPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(2)
            .saveState(false)  // 멀티 쓰레딩에서는 특정 작업이 실패한 것이 다른 작업이 성공한 것의 근거가 될 수 없음
            .queryString("SELECT u FROM User u ORDER BY u.id")
            .build();
    }
}
