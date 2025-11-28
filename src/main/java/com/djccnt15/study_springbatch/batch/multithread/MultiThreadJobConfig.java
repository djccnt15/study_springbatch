package com.djccnt15.study_springbatch.batch.multithread;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.db.model.UserEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
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
@Batch
@RequiredArgsConstructor
public class MultiThreadJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    
    @Bean
    public Job multiThreadJob(Step step) {
        return new JobBuilder("multiThreadJob", jobRepository)
            .start(step)
            .incrementer(new RunIdIncrementer())
            .build();
    }
    
    @Bean
    public Step multiThreadStep(JpaPagingItemReader<UserEntity> multiThreadReader) {
        return new StepBuilder("multiThreadStep", jobRepository)
            .<UserEntity, UserEntity>chunk(2, transactionManager)
            .reader(multiThreadReader)
            .writer(result -> log.info(result.toString()))
            .taskExecutor(new SimpleAsyncTaskExecutor())
            .build();
    }
    
    @Bean
    public JpaPagingItemReader<UserEntity> multiThreadReader() {
        return new JpaPagingItemReaderBuilder<UserEntity>()
            .name("multiThreadReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(2)
            .saveState(false)  // 멀티 쓰레딩에서는 특정 작업이 실패한 것이 다른 작업이 성공한 것의 근거가 될 수 없음
            .queryString("SELECT u FROM User u ORDER BY u.id")
            .build();
    }
}
