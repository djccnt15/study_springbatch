package com.djccnt15.study_springbatch.batch.flatfile.reader;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.reader.mapper.AbortFieldSetMapper;
import com.djccnt15.study_springbatch.batch.flatfile.reader.mapper.CollectFieldSetMapper;
import com.djccnt15.study_springbatch.batch.flatfile.reader.mapper.ErrorFieldSetMapper;
import com.djccnt15.study_springbatch.batch.flatfile.reader.model.SystemLogLine;
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
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;

@Slf4j
@Batch
@RequiredArgsConstructor
public class PatternMatchingFlatFileBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job patternMatchingJob(Step patternMatchingStep) {
        return new JobBuilder("patternMatchingJob", jobRepository)
            .start(patternMatchingStep)
            .build();
    }
    
    @Bean
    public Step patternMatchingStep(
        FlatFileItemReader<SystemLogLine> patternMatchingReader,
        ItemWriter<SystemLogLine> patternMatchingWriter
    ) {
        return new StepBuilder("patternMatchingStep", jobRepository)
            .<SystemLogLine, SystemLogLine>chunk(10, transactionManager)
            .reader(patternMatchingReader)
            .writer(patternMatchingWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<SystemLogLine> patternMatchingReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new FlatFileItemReaderBuilder<SystemLogLine>()
            .name("patternMatchingReader")
            .resource(new FileSystemResource(inputFile))
            .lineMapper(patternMatchingLineMapper())
            .build();
    }
    
    @Bean
    public PatternMatchingCompositeLineMapper<SystemLogLine> patternMatchingLineMapper() {
        var lineMapper = new PatternMatchingCompositeLineMapper<SystemLogLine>();
        
        // 라인별 패턴 매칭 및 토큰화
        var tokenizers = new HashMap<String, LineTokenizer>();
        tokenizers.put("ERROR*", errorLineTokenizer());
        tokenizers.put("ABORT*", abortLineTokenizer());
        tokenizers.put("COLLECT*", collectLineTokenizer());
        lineMapper.setTokenizers(tokenizers);
        
        // 토큰화 된 데이터를 객체로 변환
        var mappers = new HashMap<String, FieldSetMapper<SystemLogLine>>();
        mappers.put("ERROR*", new ErrorFieldSetMapper());
        mappers.put("ABORT*", new AbortFieldSetMapper());
        mappers.put("COLLECT*", new CollectFieldSetMapper());
        lineMapper.setFieldSetMappers(mappers);
        
        return lineMapper;
    }
    
    @Bean
    public DelimitedLineTokenizer errorLineTokenizer() {
        var tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "application", "errorType", "timestamp", "message", "resourceUsage", "logPath");
        return tokenizer;
    }
    
    @Bean
    public DelimitedLineTokenizer abortLineTokenizer() {
        var tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "application", "errorType", "timestamp", "message", "exitCode", "processPath", "status");
        return tokenizer;
    }
    
    @Bean
    public DelimitedLineTokenizer collectLineTokenizer() {
        var tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames("type", "dumpType", "processId", "timestamp", "dumpPath");
        return tokenizer;
    }
    
    @Bean
    public ItemWriter<SystemLogLine> patternMatchingWriter() {
        return items -> {
            for (SystemLogLine item : items) {
                System.out.println(item);
            }
        };
    }
}
