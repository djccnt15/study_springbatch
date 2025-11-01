package com.djccnt15.study_springbatch.batch.flatfile.writer;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.writer.model.FlatFileItemWriteRecord;
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
public class FlatFileRecordWriteJobConfig {
    
    @Bean
    public Job flatFileRecordWriteJob(
        JobRepository jobRepository,
        Step flatFileRecordWriteStep
    ) {
        return new JobBuilder("flatFileRecordWriteJob", jobRepository)
            .start(flatFileRecordWriteStep)
            .build();
    }
    
    @Bean
    public Step flatFileRecordWriteStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ListItemReader<FlatFileItemWriteRecord> flatFileRecordReader,
        FlatFileItemWriter<FlatFileItemWriteRecord> flatFileRecordWriter
    ) {
        return new StepBuilder("flatFileRecordWriteStep", jobRepository)
            .<FlatFileItemWriteRecord, FlatFileItemWriteRecord>chunk(10, transactionManager)
            .reader(flatFileRecordReader)
            .writer(flatFileRecordWriter)
            .build();
    }
    
    @Bean
    public ListItemReader<FlatFileItemWriteRecord> flatFileRecordReader() {
        var items = List.of(
            new FlatFileItemWriteRecord(
                "KILL-001",
                "김배치",
                "2024-01-25",
                "CPU 과부하"),
            new FlatFileItemWriteRecord(
                "KILL-002",
                "사불링",
                "2024-01-26",
                "JVM 스택오버플로우"),
            new FlatFileItemWriteRecord(
                "KILL-003",
                "박탐묘",
                "2024-01-27",
                "힙 메모리 고갈")
        );
        return new ListItemReader<>(items);
    }
    
    @Bean
    @StepScope
    public FlatFileItemWriter<FlatFileItemWriteRecord> flatFileRecordWriter(
        @Value("#{jobParameters['outputDir']}") String outputDir
    ) {
        return new FlatFileItemWriterBuilder<FlatFileItemWriteRecord>()
            .name("flatFileRecordWriter")
            .resource(new FileSystemResource(outputDir + "/flatFileItemWriteResult.csv"))
            .delimited()
            .delimiter(",")
            .sourceType(FlatFileItemWriteRecord.class)
            .names("id", "name", "date", "cause")  // 문자열로 변환할 객체의 필드 이름 지정
            .headerCallback(it -> it.write("ID,이름,일자,원인"))
            .footerCallback(it -> it.write("============"))
            .build();
    }
}
