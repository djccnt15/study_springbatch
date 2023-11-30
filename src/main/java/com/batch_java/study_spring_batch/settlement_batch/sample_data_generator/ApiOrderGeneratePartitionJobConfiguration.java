package com.batch_java.study_spring_batch.settlement_batch.sample_data_generator;

import com.batch_java.study_spring_batch.common.Batch;
import com.batch_java.study_spring_batch.model.ApiOrder;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@Slf4j
@Batch
@RequiredArgsConstructor
public class ApiOrderGeneratePartitionJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    
    @Bean
    public Job apiOrderGenerateJob(Step managerStep) {
        return new JobBuilder("apiOrderGenerateJob", jobRepository)
            .start(managerStep)
            .incrementer(new RunIdIncrementer())
            .validator(
                new DefaultJobParametersValidator(
                    new String[]{"totalCount", "targetDate"}, new String[0]
                )
            )
            .build();
    }
    
    @Bean
    @JobScope
    public Step managerStep(
        PartitionHandler partitionHandler,
        @Value("#{jobParameters['targetDate']}") String targetDate,
        Step apiOrderGenerateStep
    ) {
        return new StepBuilder("managerStep", jobRepository)
            .partitioner("delegateStep", getPartitioner(targetDate))
            .step(apiOrderGenerateStep)
            .partitionHandler(partitionHandler)
            .build();
    }
    
    // define how managerStep handle workerStep
    @Bean
    public PartitionHandler partitionHandler(Step apiOrderGenerateStep) {
        final TaskExecutorPartitionHandler taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(apiOrderGenerateStep);
        taskExecutorPartitionHandler.setGridSize(7);
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return taskExecutorPartitionHandler;
    }
    
    // interface create StepExecution for workerStep
    Partitioner getPartitioner(String targetDate) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final LocalDate date = LocalDate.parse(targetDate, formatter);
        
        return gridSize -> {
            final Map<String, ExecutionContext> result = new HashMap<>();
            
            IntStream.range(0, 7)
                .forEach(it -> {
                    final ExecutionContext value = new ExecutionContext();
                    value.putString("targetDate", date.minusDays(it).format(formatter));
                    result.put("partition" + it, value);
                });
            
            return result;
        };
    }
    
    @Bean
    public Step apiOrderGenerateStep(
        ApiOrderGenerateReader apiOrderGenerateReader,
        ApiOrderGenerateProcessor apiOrderGenerateProcessor,
        ItemWriter<ApiOrder> jpaItemWriter
    ) {
        return new StepBuilder("apiOrderGenerateStep", jobRepository)
            .<String, ApiOrder>chunk(1000, platformTransactionManager)
            .reader(apiOrderGenerateReader)
            .processor(apiOrderGenerateProcessor)
            .writer(jpaItemWriter)
            .build();
    }
    
    @Bean
    public ItemWriter<ApiOrder> jpaItemWriter(
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaItemWriterBuilder<ApiOrder>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
}
