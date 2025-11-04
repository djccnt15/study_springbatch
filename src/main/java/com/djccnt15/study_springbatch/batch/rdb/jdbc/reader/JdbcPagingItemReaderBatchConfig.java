package com.djccnt15.study_springbatch.batch.rdb.jdbc.reader;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.rdb.jdbc.reader.model.TargetProcess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class JdbcPagingItemReaderBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    
    @Bean
    public Job jdbcPagingItemReaderJob() {
        return new JobBuilder("jdbcPagingItemReaderJob", jobRepository)
            .start(jdbcPagingItemReaderStep())
            .build();
    }
    
    @Bean
    public Step jdbcPagingItemReaderStep() {
        return new StepBuilder("jdbcPagingItemReaderStep", jobRepository)
            .<TargetProcess, TargetProcess>chunk(5, transactionManager)
            .reader(jdbcPagingItemReader())
            .writer(jdbcPagingItemReaderWriter())
            .build();
    }
    
    @Bean
    public JdbcPagingItemReader<TargetProcess> jdbcPagingItemReader() {
        return new JdbcPagingItemReaderBuilder<TargetProcess>()
            .name("jdbcPagingItemReader")
            .dataSource(dataSource)
            .pageSize(5)
            .selectClause("SELECT id, name, process_id, terminated_at, status")
            .fromClause("FROM target_process")
            .whereClause("WHERE status = :status AND terminated_at <= :terminatedAt")
            // Keyset Pagination 방식이기 때문에 정렬 키가 필수
            .sortKeys(Map.of("id", Order.ASCENDING))
            .parameterValues(Map.of(
                "status", "TERMINATED",
                "terminatedAt", LocalDateTime.now()
            ))
            .dataRowMapper(TargetProcess.class)
            .build();
    }
    
    @Bean
    public ItemWriter<TargetProcess> jdbcPagingItemReaderWriter() {
        return items -> {
            for (TargetProcess item : items) {
                System.out.println(item);
            }
        };
    }
}
