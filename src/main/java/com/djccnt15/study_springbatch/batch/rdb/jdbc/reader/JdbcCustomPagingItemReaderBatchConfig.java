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
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class JdbcCustomPagingItemReaderBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    
    @Bean
    public Job jdbcCustomPagingItemReaderJob() {
        return new JobBuilder("jdbcCustomPagingItemReaderJob", jobRepository)
            .start(jdbcCustomPagingItemReaderStep())
            .build();
    }
    
    @Bean
    public Step jdbcCustomPagingItemReaderStep() {
        return new StepBuilder("jdbcCustomPagingItemReaderStep", jobRepository)
            .<TargetProcess, TargetProcess>chunk(5, transactionManager)
            .reader(jdbcCustomPagingItemReader())
            .writer(jdbcCustomPagingItemReaderWriter())
            .build();
    }
    
    @Bean
    public JdbcPagingItemReader<TargetProcess> jdbcCustomPagingItemReader() {
        return new JdbcPagingItemReaderBuilder<TargetProcess>()
            .name("jdbcCustomPagingItemReader")
            .dataSource(dataSource)
            .pageSize(5)
            .queryProvider(pagingQueryProvider(dataSource))
            .parameterValues(Map.of(
                "status", "TERMINATED",
                "terminatedAt", LocalDateTime.now()
            ))
            .dataRowMapper(TargetProcess.class)
            .build();
    }
    
    @Bean
    public ItemWriter<TargetProcess> jdbcCustomPagingItemReaderWriter() {
        return items -> {
            for (TargetProcess item : items) {
                System.out.println(item);
            }
        };
    }
    
    private PagingQueryProvider pagingQueryProvider(DataSource dataSource) {
        var queryProviderFactory = new SqlPagingQueryProviderFactoryBean();
        // 데이터베이스 타입에 맞는 적절한 PagingQueryProvider 구현체를 생성할 수 있도록 dataSource 전달
        queryProviderFactory.setDataSource(dataSource);
        queryProviderFactory.setSelectClause("SELECT id, name, process_id, terminated_at, status");
        queryProviderFactory.setFromClause("FROM target_process");
        queryProviderFactory.setWhereClause("WHERE status = :status AND terminated_at <= :terminatedAt");
        queryProviderFactory.setSortKeys(Map.of("id", Order.ASCENDING));
        try {
            return queryProviderFactory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
