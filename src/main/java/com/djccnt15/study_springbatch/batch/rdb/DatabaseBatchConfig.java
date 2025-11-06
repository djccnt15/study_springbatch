package com.djccnt15.study_springbatch.batch.rdb;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.rdb.converter.UserConverter;
import com.djccnt15.study_springbatch.db.model.UserEntity;
import com.djccnt15.study_springbatch.db.model.UserNewEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Batch
@RequiredArgsConstructor
public class DatabaseBatchConfig {
    
    private final UserConverter userConverter;
    
    @Bean
    public Job databaseBatchJob(
        JobRepository jobRepository,
        Step databaseBatchStep
    ) {
        return new JobBuilder("databaseBatchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(databaseBatchStep)
            .build();
    }
    
    @Bean
    public Step databaseBatchStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<UserEntity> jpaCursorItemReader,
        ItemProcessor<UserEntity, UserNewEntity> databaseBatchItemProcessor,
        ItemWriter<UserNewEntity> jdbcItemWriter
    ) {
        return new StepBuilder("databaseBatchStep", jobRepository)
            .<UserEntity, UserNewEntity>chunk(2, transactionManager)
            .reader(jpaCursorItemReader)
            .processor(databaseBatchItemProcessor)
            // .writer(System.out::println)
            .writer(jdbcItemWriter)
            .faultTolerant()
            .skip(DuplicateKeyException.class)
            .skipPolicy(
                (t, skipCount) -> t instanceof DuplicateKeyException && skipCount < Integer.MAX_VALUE
            )
            .build();
    }
    
    // JpaPagingItemReader는 내부적으로 `OFFSET` 기반 페이징을 사용하기 때문에 정합성/성능 측면에서 문제를 발생시킴. 비추천
    @Bean
    public ItemReader<UserEntity> jpaPagingItemReader(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaPagingItemReaderBuilder<UserEntity>()
            .name("jpaPagingItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT u FROM User u ORDER BY u.id")  // 실제 테이블 명 x, JPA 클래스 이름 사용
            .pageSize(3)
            .transacted(false)  // true일 경우 read 중간에 commit이 발생해 reader가 데이터를 변경할 수 있음
            .build();
    }

    @Bean
    public ItemReader<UserEntity> jpaCursorItemReader(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaCursorItemReaderBuilder<UserEntity>()
            .name("jpaCursorItemReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT u FROM User u ORDER BY u.id")
            .build();
    }

    @Bean
    public ItemWriter<UserNewEntity> jpaItemWriter(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaItemWriterBuilder<UserNewEntity>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
    
    @Bean
    public ItemWriter<UserNewEntity> jdbcItemWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<UserNewEntity>()
            .dataSource(dataSource)
            .sql("""
                INSERT INTO USER_NEW
                VALUES (:id, :name, :age, :region, :phoneNumber)
                """)
            .beanMapped()
            .build();
    }
    
    @Bean
    public ItemProcessor<UserEntity, UserNewEntity> databaseBatchItemProcessor() {
        return user -> {
            user.setAge(user.getAge() * 2);
            return userConverter.toNewUser(user);
        };
    }
}
