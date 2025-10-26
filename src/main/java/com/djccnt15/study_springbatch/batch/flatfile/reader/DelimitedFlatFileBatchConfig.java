package com.djccnt15.study_springbatch.batch.flatfile.reader;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.model.SystemFailureLine;
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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class DelimitedFlatFileBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job delimitedFlatFileJob(Step delimitedFlatFileStep) {
        return new JobBuilder("delimitedFlatFileJob", jobRepository)
            .start(delimitedFlatFileStep)
            .build();
    }
    
    @Bean
    public Step delimitedFlatFileStep(
        FlatFileItemReader<SystemFailureLine> delimitedFlatFileItemReader,
        SystemFailureStdoutItemWriter delimitedFlatFileItemWriter
    ) {
        return new StepBuilder("delimitedFlatFileStep", jobRepository)
            // 파일의 한 줄을 읽어 이를 처리하는 작업을 청크당 10번 반복
            .<SystemFailureLine, SystemFailureLine>chunk(10, transactionManager)
            .reader(delimitedFlatFileItemReader)
            .writer(delimitedFlatFileItemWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<SystemFailureLine> delimitedFlatFileItemReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new FlatFileItemReaderBuilder<SystemFailureLine>()
            .name("delimitedFlatFileItemReader")  // 현재 스텝 실행의 진행 상황 추적 등에 사용
            .resource(new FileSystemResource(inputFile))
            .delimited()
            .delimiter(",")
            // FieldSet의 names 필드에 사용할 객체의 프로퍼티 이름을 전달
            .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
            .targetType(SystemFailureLine.class)  // 매핑 대상 클래스 지정
            .linesToSkip(1)  // 헤더 처리
            // .comments()  // 주석 처리. 주석 표기 지정
            // true 설정 시 tokens의 길이와 names()에 전달된 프로퍼티의 길이가 다를 경우 에러, false 시 자동 보정
            // .strict(true)  // 파일 누락 시 예외 발생
            .build();
    }
    
    @Bean
    public SystemFailureStdoutItemWriter delimitedFlatFileItemWriter() {
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
