package com.djccnt15.study_springbatch.batch.flatfile.writer;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.model.FlatFileItemWriteModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Batch
public class FlatFileCustomWriteJobConfig {
    
    @Bean
    public Job flatFileCustomWriteJob(
        JobRepository jobRepository,
        Step flatFileCustomWriteStep
    ) {
        return new JobBuilder("flatFileCustomWriteJob", jobRepository)
            .start(flatFileCustomWriteStep)
            .build();
    }
    
    @Bean
    public Step flatFileCustomWriteStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ListItemReader<FlatFileItemWriteModel> flatFileCustomReader,
        FlatFileItemWriter<FlatFileItemWriteModel> flatFileCustomWriter
    ) {
        return new StepBuilder("flatFileCustomWriteStep", jobRepository)
            .<FlatFileItemWriteModel, FlatFileItemWriteModel>chunk(10, transactionManager)
            .reader(flatFileCustomReader)
            .writer(flatFileCustomWriter)
            .build();
    }
    
    @Bean
    public ListItemReader<FlatFileItemWriteModel> flatFileCustomReader() {
        var items = List.of(
            new FlatFileItemWriteModel(
                "KILL-001",
                "김배치",
                "2024-01-25",
                "CPU 과부하"),
            new FlatFileItemWriteModel(
                "KILL-002",
                "사불링",
                "2024-01-26",
                "JVM 스택오버플로우"),
            new FlatFileItemWriteModel(
                "KILL-003",
                "박탐묘",
                "2024-01-27",
                "힙 메모리 고갈")
        );
        return new ListItemReader<>(items);
    }
    
    @Bean
    @StepScope
    public FlatFileItemWriter<FlatFileItemWriteModel> flatFileCustomWriter(
        @Value("#{jobParameters['outputDir']}") String outputDir
    ) {
        return new FlatFileItemWriterBuilder<FlatFileItemWriteModel>()
            .name("flatFileCustomWriter")
            .resource(new FileSystemResource(outputDir + "/flatFileItemCustomWriteResult.txt"))
            .formatted()
            .format("ID: %s | 일자: %s | 대상: %s | 원인: %s")  // custom 포멧 지정
            .sourceType(FlatFileItemWriteModel.class)
            .names("id", "date", "name", "cause")
            .headerCallback(it -> it.write("================= 실행 ================="))
            .footerCallback(it -> it.write("================= 완료 =================="))
            .build();
    }
}
