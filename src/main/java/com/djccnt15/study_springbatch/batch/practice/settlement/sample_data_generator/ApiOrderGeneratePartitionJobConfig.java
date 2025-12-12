package com.djccnt15.study_springbatch.batch.practice.settlement.sample_data_generator;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.db.model.ApiOrderEntity;
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
import java.util.stream.IntStream;

@Slf4j
@Batch
@RequiredArgsConstructor
public class ApiOrderGeneratePartitionJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final EntityManagerFactory entityManagerFactory;
    
    @Bean
    public Job apiOrderGenerateJob(Step managerStep) {
        return new JobBuilder("apiOrderGenerateJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(managerStep)
            .validator(
                new DefaultJobParametersValidator(
                    new String[]{"totalCount", "targetDate"}, new String[0]
                )
            )
            .build();
    }
    
    @Bean
    @JobScope
    public Step apiOrderManagerStep(
        PartitionHandler apiOrderPartitionHandler,
        @Value("#{jobParameters['targetDate']}") String targetDate,
        Step apiOrderGenerateStep
    ) {
        return new StepBuilder("apiOrderManagerStep", jobRepository)
            .partitioner("delegateStep", getPartitioner(targetDate))
            .step(apiOrderGenerateStep)
            .partitionHandler(apiOrderPartitionHandler)
            .build();
    }
    
    // define how managerStep handle workerStep
    @Bean
    public PartitionHandler apiOrderPartitionHandler(Step apiOrderGenerateStep) {
        var taskExecutorPartitionHandler = new TaskExecutorPartitionHandler();
        taskExecutorPartitionHandler.setStep(apiOrderGenerateStep);
        taskExecutorPartitionHandler.setGridSize(7);
        taskExecutorPartitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return taskExecutorPartitionHandler;
    }
    
    // interface create StepExecution for workerStep
    private Partitioner getPartitioner(String targetDate) {
        var formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        var date = LocalDate.parse(targetDate, formatter);
        
        return gridSize -> {
            var result = new HashMap<String, ExecutionContext>();
            
            IntStream.range(0, 7).forEach(it -> {
                var value = new ExecutionContext();
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
        ItemWriter<ApiOrderEntity> apiOrderJpaItemWriter
    ) {
        return new StepBuilder("apiOrderGenerateStep", jobRepository)
            .<String, ApiOrderEntity>chunk(1000, platformTransactionManager)
            .reader(apiOrderGenerateReader)
            .processor(apiOrderGenerateProcessor)
            .writer(apiOrderJpaItemWriter)
            .build();
    }
    
    @Bean
    public ItemWriter<ApiOrderEntity> apiOrderJpaItemWriter() {
        return new JpaItemWriterBuilder<ApiOrderEntity>()
            .entityManagerFactory(entityManagerFactory)
            .build();
    }
}
