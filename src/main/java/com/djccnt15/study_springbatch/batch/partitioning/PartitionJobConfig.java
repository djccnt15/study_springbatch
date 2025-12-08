package com.djccnt15.study_springbatch.batch.partitioning;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.partitioning.partitioner.ColumnRangePartitioner;
import com.djccnt15.study_springbatch.db.model.UserEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class PartitionJobConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;
    
    @Bean
    public Job partitionJob(Step managerStep) {
        return new JobBuilder("partitionJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(managerStep)
            .build();
    }
    
    @Bean
    public Step managerStep(
        Step partitionStep,
        PartitionHandler partitionHandler
    ) {
        return new StepBuilder("managerStep", jobRepository)
            .partitioner("delegateStep", new ColumnRangePartitioner(dataSource))
            .step(partitionStep)
            .partitionHandler(partitionHandler)
            .build();
    }
    
    @Bean
    public PartitionHandler partitionHandler(Step partitionStep) {
        var taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(partitionStep);
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        taskExecutorPartitionHandler.setGridSize(5);
        return taskExecutorPartitionHandler;
    }
    
    @Bean
    public Step partitionStep(JpaPagingItemReader<UserEntity> partitionItemReader) {
        return new StepBuilder("partitionStep", jobRepository)
            .<UserEntity, UserEntity>chunk(4, transactionManager)
            .reader(partitionItemReader)
            .writer(result -> log.info(result.toString()))
            .build();
    }
    
    @Bean
    @StepScope
    public JpaPagingItemReader<UserEntity> partitionItemReader(
        @Value("#{stepExecutionContext[minValue]}") Long minValue,
        @Value("#{stepExecutionContext[maxValue]}") Long maxValue
    ) {
        log.info("minValue: {}, maxValue: {}", minValue, maxValue);
        
        var params = new HashMap<String, Object>();
        params.put("minValue", minValue);
        params.put("maxValue", maxValue);
        
        return new JpaPagingItemReaderBuilder<UserEntity>()
            .name("partitionItemReader")
            .entityManagerFactory(entityManagerFactory)
            .pageSize(5)
            .queryString("""
                SELECT u FROM UserEntity u
                WHERE 1=1
                AND u.id BETWEEN :minValue and :maxValue
                """)
            .parameterValues(params)
            .build();
    }
}
