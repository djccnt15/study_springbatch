package com.djccnt15.study_springbatch.batch.flatfile.reader;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.reader.model.SystemFailureLine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class MultiResourceFlatFileReaderBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job multiResourceFlatFileReaderJob(Step multiResourceFlatFileReaderStep) {
        return new JobBuilder("multiResourceFlatFileReaderJob", jobRepository)
            .start(multiResourceFlatFileReaderStep)
            .build();
    }
    @Bean
    public Step multiResourceFlatFileReaderStep(
        MultiResourceItemReader<SystemFailureLine> multiResourceFlatFileItemReader,
        SystemFailureStdoutItemWriter multiResourceFlatFileItemReaderWriter
    ) {
        return new StepBuilder("multiResourceFlatFileReaderStep", jobRepository)
            .<SystemFailureLine, SystemFailureLine>chunk(10, transactionManager)
            .reader(multiResourceFlatFileItemReader)
            .writer(multiResourceFlatFileItemReaderWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public MultiResourceItemReader<SystemFailureLine> multiResourceFlatFileItemReader(
        @Value("#{jobParameters['inputFilePath']}") String inputFilePath
    ) {
        return new MultiResourceItemReaderBuilder<SystemFailureLine>()
            .name("multiResourceFlatFileItemReader")
            .resources(new Resource[]{
                new FileSystemResource(inputFilePath + "/multi_resource_1.csv"),
                new FileSystemResource(inputFilePath + "/multi_resource_2.csv")
            })
            .comparator((r1, r2) -> {  // 미지정 시 파일명의 알파벳 순서로 파일 읽기
                // 파일명 역순으로 정렬 (normal -> critical 순서)
                return r2.getFilename().compareTo(r1.getFilename());
            })
            .delegate(multiResourceFlatFileReader())
            .build();
    }
    
    @Bean
    public FlatFileItemReader<SystemFailureLine> multiResourceFlatFileReader() {
        // 읽을 파일의 대상을 MultiResourceItemReader에서 지정하기 때문에 resource() 미사용
        return new FlatFileItemReaderBuilder<SystemFailureLine>()
            .name("multiResourceFlatFileReader")
            .delimited()
            .delimiter(",")
            .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
            .targetType(SystemFailureLine.class)
            .linesToSkip(1)
            .build();
    }
    
    @Bean
    public SystemFailureStdoutItemWriter multiResourceFlatFileItemReaderWriter() {
        return new SystemFailureStdoutItemWriter();
    }
    
    public static class SystemFailureStdoutItemWriter implements ItemWriter<SystemFailureLine> {
        @Override
        public void write(Chunk<? extends SystemFailureLine> chunk) throws Exception {
            for (SystemFailureLine failure : chunk) {
                System.out.println(failure);
            }
        }
    }
}
