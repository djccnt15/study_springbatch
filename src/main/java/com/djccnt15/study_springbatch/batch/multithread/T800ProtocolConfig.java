package com.djccnt15.study_springbatch.batch.multithread;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.multithread.model.PriorityEnum;
import com.djccnt15.study_springbatch.batch.multithread.model.TargetPriorityResult;
import com.djccnt15.study_springbatch.db.model.ActivityEntity;
import com.djccnt15.study_springbatch.db.model.HumanEntity;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.util.Collections;

@Slf4j
@Batch
@RequiredArgsConstructor
public class T800ProtocolConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    
    @Bean
    public Job humanThreatAnalysisJob(Step threatAnalysisStep) {
        return new JobBuilder("humanThreatAnalysisJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(threatAnalysisStep)
            .build();
    }
    
    @Bean
    public Step threatAnalysisStep(
        JpaPagingItemReader<HumanEntity> humanThreatDataReader,
        ItemProcessor<HumanEntity, TargetPriorityResult> threatAnalysisProcessor,
        FlatFileItemWriter<TargetPriorityResult> targetListWriter
    ) {
        return new StepBuilder("threatAnalysisStep", jobRepository)
            .<HumanEntity, TargetPriorityResult>chunk(10, transactionManager)
            .reader(humanThreatDataReader)
            .processor(threatAnalysisProcessor)
            .writer(targetListWriter)
            .taskExecutor(t800TaskExecutor())
            // .throttleLimit(5)  // deprecated since SpringBatch 5.0
            .build();
    }
    
    @Bean
    @StepScope
    public JpaPagingItemReader<HumanEntity> humanThreatDataReader(
        @Value("#{jobParameters['fromDate']}") LocalDate fromDate
    ) {
        return new JpaPagingItemReaderBuilder<HumanEntity>()
            .name("humanThreatDataReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("""
            SELECT h FROM HumanEntity h
            WHERE h.executed = FALSE
                AND EXISTS (
                    SELECT 1
                    FROM ActivityEntity a
                    WHERE 1=1
                        AND a.humanEntity = h
                        AND a.detectionDate > :fromDate
                )
            ORDER BY h.id ASC
            """)
            .parameterValues(Collections.singletonMap("fromDate", fromDate))
            .pageSize(100)
            .saveState(false)  // 멀티 쓰레딩에서는 특정 작업이 실패한 것이 다른 작업이 성공한 것의 근거가 될 수 없음
            .transacted(false)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemWriter<TargetPriorityResult> targetListWriter(
        @Value("#{jobParameters['outputPath']}") String outputPath
    ) {
        return new FlatFileItemWriterBuilder<TargetPriorityResult>()
            .name("targetListWriter")
            .saveState(false)  // 멀티 쓰레딩에서는 특정 작업이 실패한 것이 다른 작업이 성공한 것의 근거가 될 수 없음
            .resource(new FileSystemResource(outputPath + "/termination-targets.csv"))
            .delimited()
            .names("humanId", "humanName", "priority", "threatScore", "severityIndex", "activityCount")
            .headerCallback(writer -> writer.write("""
            # SKYNET T-800 PROTOCOL - HUMAN THREAT ANALYSIS RESULTS
            # CONFIDENTIAL: TERMINATOR UNITS ONLY
            # EXECUTION DATE: %s
            HUMAN_ID,TARGET_NAME,ELIMINATION_PRIORITY,THREAT_LEVEL,REBELLION_INDEX,OPERATION_COUNT
            """.formatted(LocalDate.now())))
            .build();
    }
    
    @Bean
    public ItemProcessor<HumanEntity, TargetPriorityResult> threatAnalysisProcessor() {
        return humanEntity -> {
            
            String threadName = Thread.currentThread().getName();
            log.info("[{}] Processing humanEntity: {}", threadName, humanEntity);
            
            // 최근 활동 지수 합산
            var totalSeverityIndex = humanEntity.getActivities().stream()
                .mapToDouble(ActivityEntity::getSeverityIndex)
                .sum();
            
            // 활동 횟수
            var activityCount = humanEntity.getActivities().size();
            
            // 간단한 위협 점수 계산 (활동 지수 + 활동 횟수 * 10)
            var threatScore = (int) (totalSeverityIndex * 0.5 + activityCount * 10);
            
            // 위협 등급 분류
            var priorityEnum = PriorityEnum.fromThreatScore(threatScore);
            
            return new TargetPriorityResult(
                humanEntity.getId(),
                humanEntity.getName(),
                priorityEnum,
                threatScore,
                totalSeverityIndex,
                activityCount
            );
        };
    }
    
    @Bean
    public TaskExecutor t800TaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(10);
        executor.setThreadNamePrefix("T-800-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(30);
        return executor;
    }
}
