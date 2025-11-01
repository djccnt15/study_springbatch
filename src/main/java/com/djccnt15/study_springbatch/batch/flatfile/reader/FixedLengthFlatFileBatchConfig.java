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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class FixedLengthFlatFileBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job fixedLengthFlatFileJob(Step fixedLengthFlatFileStep) {
        return new JobBuilder("fixedLengthFlatFileJob", jobRepository)
            .start(fixedLengthFlatFileStep)
            .build();
    }
    
    @Bean
    public Step fixedLengthFlatFileStep(
        FlatFileItemReader<SystemFailureLine> fixedLengthFlatFileItemReader,
        SystemFailureStdoutItemWriter fixedLengthFlatFileItemWriter
    ) {
        return new StepBuilder("fixedLengthFlatFileStep", jobRepository)
            // 파일의 한 줄을 읽어 이를 처리하는 작업을 청크당 10번 반복
            .<SystemFailureLine, SystemFailureLine>chunk(10, transactionManager)
            .reader(fixedLengthFlatFileItemReader)
            .writer(fixedLengthFlatFileItemWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<SystemFailureLine> fixedLengthFlatFileItemReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new FlatFileItemReaderBuilder<SystemFailureLine>()
            .name("fixedLengthFlatFileItemReader")
            .resource(new FileSystemResource(inputFile))
            .fixedLength()  // FixedLengthTokenizer 사용. 공백 trim 처리
            .columns(new Range[]{  // 각 필드의 정확한 위치를 FixedLengthTokenizer에 전달
                new Range(1, 6),     // errorId: ERR001
                new Range(8, 26),    // errorDateTime: 날짜시간
                new Range(28, 35),   // severity: CRITICAL/FATAL + 패딩
                new Range(37, 40),   // processId: 1234
                new Range(42, 56)    // errorMessage: 패딩 + 메시지 + \n
            })
            .names("errorId", "errorDateTime", "severity", "processId", "errorMessage")
            .targetType(SystemFailureLine.class)
            .customEditors(Map.of(LocalDateTime.class, dateTimeEditor()))
            // .comments()  // 주석 처리. 주석 표기 지정
            // true 설정 시 파일에서 읽은 라인의 길이가 Range에 지정된 최대 길이(마지막 필드의 끝 위치)와 다를 경우 예외 발생
            // .strict(true)  // 파일 누락 시 예외 발생
            .build();
    }
    
    
    @Bean
    public SystemFailureStdoutItemWriter fixedLengthFlatFileItemWriter() {
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
    
    private PropertyEditor dateTimeEditor() {
        return new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                setValue(LocalDateTime.parse(text, formatter));
            }
        };
    }
}
