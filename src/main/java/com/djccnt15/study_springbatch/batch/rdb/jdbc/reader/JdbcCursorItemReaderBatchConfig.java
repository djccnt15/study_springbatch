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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Batch
@RequiredArgsConstructor
public class JdbcCursorItemReaderBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    
    @Bean
    public Job jdbcCursorItemReaderJob() {
        return new JobBuilder("jdbcCursorItemReaderJob", jobRepository)
            .start(jdbcCursorItemReaderStep())
            .build();
    }
    
    @Bean
    public Step jdbcCursorItemReaderStep() {
        return new StepBuilder("jdbcCursorItemReaderStep", jobRepository)
            .<TargetProcess, TargetProcess>chunk(5, transactionManager)
            .reader(jdbcCursorItemReader())
            .writer(jdbcCursorItemReaderWriter())
            .build();
    }
    
    @Bean
    public JdbcCursorItemReader<TargetProcess> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<TargetProcess>()
            .name("jdbcCursorItemReader")
            .dataSource(dataSource)
            .sql("""
                SELECT id, name, process_id, terminated_at, status
                FROM target_process
                WHERE status = ? AND terminated_at <= ?
                """)
            .queryArguments(List.of("TERMINATED", LocalDateTime.now()))
            // .beanRowMapper(TargetProcess.class)
            .dataRowMapper(TargetProcess.class)
            .build();
    }
    
    @Bean
    public ItemWriter<TargetProcess> jdbcCursorItemReaderWriter() {
        return items -> {
            for (TargetProcess item : items) {
                System.out.println(item);
            }
        };
    }
}
