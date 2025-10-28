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
public class FlatFileItemWriteJobConfig {
    
    @Bean
    public Job flatFileItemWriteJob(
        JobRepository jobRepository,
        Step flatFileItemWriteStep
    ) {
        return new JobBuilder("flatFileItemWriteJob", jobRepository)
            .start(flatFileItemWriteStep)
            .build();
    }
    
    @Bean
    public Step flatFileItemWriteStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ListItemReader<FlatFileItemWriteModel> flatFileItemWriteReader,
        FlatFileItemWriter<FlatFileItemWriteModel> flatFileItemWriteWriter
    ) {
        return new StepBuilder("flatFileItemWriteStep", jobRepository)
            .<FlatFileItemWriteModel, FlatFileItemWriteModel>chunk(10, transactionManager)
            .reader(flatFileItemWriteReader)
            .writer(flatFileItemWriteWriter)
            .build();
    }
    
    @Bean
    public ListItemReader<FlatFileItemWriteModel> flatFileItemWriteReader() {
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
    public FlatFileItemWriter<FlatFileItemWriteModel> flatFileItemWriteWriter(
        @Value("#{jobParameters['outputDir']}") String outputDir
    ) {
        return new FlatFileItemWriterBuilder<FlatFileItemWriteModel>()
            .name("flatFileItemWriteWriter")
            .resource(new FileSystemResource(outputDir + "/flatFileItemWriteResult.csv"))
            .delimited()
            .delimiter(",")
            .sourceType(FlatFileItemWriteModel.class)
            .names("id", "name", "date", "cause")
            .headerCallback(writer -> writer.write("ID,이름,일자,원인"))
            .footerCallback(it -> it.write("============"))
            .build();
    }
}
