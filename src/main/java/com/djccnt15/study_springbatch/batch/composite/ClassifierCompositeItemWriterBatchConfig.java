package com.djccnt15.study_springbatch.batch.composite;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.composite.model.SystemLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;

@Slf4j
@Batch
@RequiredArgsConstructor
public class ClassifierCompositeItemWriterBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job systemLogProcessingJob() {
        return new JobBuilder("systemLogProcessingJob", jobRepository)
            .start(systemLogProcessingStep())
            .build();
    }
    
    @Bean
    public Step systemLogProcessingStep() {
        return new StepBuilder("systemLogProcessingStep", jobRepository)
            .<SystemLog, SystemLog>chunk(10, transactionManager)
            .reader(systemLogProcessingReader())
            .writer(classifierWriter())
            .build();
    }
    
    @Bean
    public ListItemReader<SystemLog> systemLogProcessingReader() {
        var logs = new ArrayList<SystemLog>();
        
        var criticalLog = SystemLog.builder()
            .type("CRITICAL")
            .message("OOM 발생")
            .cpuUsage(95)
            .memoryUsage(2024 * 1024 * 1024L)
            .build();
        logs.add(criticalLog);
        
        var normalLog = SystemLog.builder()
            .type("NORMAL")
            .message("시스템 정상 작동 중")
            .cpuUsage(30)
            .memoryUsage(512 * 1024 * 1024L)
            .build();
        logs.add(normalLog);
        
        return new ListItemReader<>(logs);
    }
    
    @Bean
    public ClassifierCompositeItemWriter<SystemLog> classifierWriter() {
        var writer = new ClassifierCompositeItemWriter<SystemLog>();
        writer.setClassifier(new SystemLogClassifier(criticalLogWriter(), normalLogWriter()));
        return writer;
    }
    
    @Bean
    public ItemWriter<SystemLog> normalLogWriter() {
        return items -> {
            log.info("NoramLogWriter: 일반 로그 처리 중...");
            for (SystemLog item : items) {
                log.info("일반 처리: {}", item);
            }
        };
    }
    
    @Bean
    public ItemWriter<SystemLog> criticalLogWriter() {
        return items -> {
            log.info("CriticalLogWriter: 치명적 시스템 로그 감지!");
            for (SystemLog item : items) {
                // 실제 운영에선 여기서 슬랙 혹은 이메일 발송
                log.info("긴급 처리: {}", item);
            }
        };
    }
}
