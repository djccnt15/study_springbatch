package com.djccnt15.study_springbatch.batch.flatfile.reader;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.model.RecordMatchingRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class RecordFlatFileBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job recordMatchingJob(Step recordMatchingStep) {
        return new JobBuilder("recordMatchingJob", jobRepository)
            .start(recordMatchingStep)
            .build();
    }
    
    @Bean
    public Step recordMatchingStep(
        FlatFileItemReader<RecordMatchingRecord> recordMatchingReader,
        ItemWriter<RecordMatchingRecord> recordMatchingWriter
    ) {
        return new StepBuilder("recordMatchingStep", jobRepository)
            .<RecordMatchingRecord, RecordMatchingRecord>chunk(10, transactionManager)
            .reader(recordMatchingReader)
            .writer(recordMatchingWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<RecordMatchingRecord> recordMatchingReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new FlatFileItemReaderBuilder<RecordMatchingRecord>()
            .name("recordMatchingReader")
            .resource(new FileSystemResource(inputFile))
            .delimited()
            .names("command", "cpu", "status")
            .targetType(RecordMatchingRecord.class)
            .linesToSkip(1)
            .build();
    }
    
    @Bean
    public ItemWriter<RecordMatchingRecord> recordMatchingWriter() {
        return items -> {
            for (RecordMatchingRecord item : items) {
                System.out.println(item);
            }
        };
    }
}
