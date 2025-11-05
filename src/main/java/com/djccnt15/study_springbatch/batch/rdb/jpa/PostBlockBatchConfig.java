package com.djccnt15.study_springbatch.batch.rdb.jpa;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.db.model.BlockedPostEntity;
import com.djccnt15.study_springbatch.db.model.PostEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.orm.JpaNamedQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class PostBlockBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    
    @Bean
    public Job postBlockBatchJob(Step postBlockStep) {
        return new JobBuilder("postBlockBatchJob", jobRepository)
            .start(postBlockStep)
            .build();
    }
    
    @Bean
    public Step postBlockStep(
        JpaCursorItemReader<PostEntity> postBlockReader,
        PostBlockProcessor postBlockProcessor,
        ItemWriter<BlockedPostEntity> postBlockWriter
    ) {
        return new StepBuilder("postBlockStep", jobRepository)
            .<PostEntity, BlockedPostEntity>chunk(5, transactionManager)
            .reader(postBlockReader)
            .processor(postBlockProcessor)
            .writer(postBlockWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public JpaCursorItemReader<PostEntity> postBlockReader(
        @Value("#{jobParameters['startDateTime']}") LocalDateTime startDateTime,
        @Value("#{jobParameters['endDateTime']}") LocalDateTime endDateTime
    ) {
        return new JpaCursorItemReaderBuilder<PostEntity>()
            .name("postBlockReader")
            .entityManagerFactory(entityManagerFactory)
            // .queryProvider(createQueryProvider())
            .queryString("""
                SELECT p FROM PostEntity p JOIN FETCH p.reports r
                WHERE r.reportedAt >= :startDateTime AND r.reportedAt < :endDateTime
                """)
            .parameterValues(Map.of(
                "startDateTime", startDateTime,
                "endDateTime", endDateTime
            ))
            .build();
    }
    
    private JpaNamedQueryProvider<PostEntity> createQueryProvider() {
        JpaNamedQueryProvider<PostEntity> queryProvider = new JpaNamedQueryProvider<>();
        queryProvider.setEntityClass(PostEntity.class);
        queryProvider.setNamedQuery("PostEntity.findByReportsReportedAtBetween");
        return queryProvider;
    }
    
    @Bean
    public ItemWriter<BlockedPostEntity> postBlockWriter() {
        return items -> items.forEach(blockedPost -> {
            log.info("TERMINATED: [ID:{}] '{}' by {} | 신고:{}건 | 점수:{} | kill -9 at {}",
                blockedPost.getPostId(),
                blockedPost.getTitle(),
                blockedPost.getWriter(),
                blockedPost.getReportCount(),
                String.format("%.2f", blockedPost.getBlockScore()),
                blockedPost.getBlockedAt().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
    }
    
    @Bean
    public JpaItemWriter<BlockedPostEntity> postBlockJpaWriter() {
        return new JpaItemWriterBuilder<BlockedPostEntity>()
            .entityManagerFactory(entityManagerFactory)
            // 새로운 데이터 추가 시(INSERT) -> true ->> persist()
            // 기존 데이터 수정 시(UPDATE) -> false ->> merge()
            .usePersist(true)
            .build();
    }
}
