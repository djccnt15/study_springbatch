package com.djccnt15.study_springbatch.batch.flatfile.reader;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.model.RegexLogLine;
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
import org.springframework.batch.item.file.transform.RegexLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class RegexFlatFileBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job regexFlatFileJob(Step regexFlatFileStep) {
        return new JobBuilder("regexFlatFileJob", jobRepository)
            .start(regexFlatFileStep)
            .build();
    }
    
    @Bean
    public Step regexFlatFileStep(
        FlatFileItemReader<RegexLogLine> logItemReader,
        ItemWriter<RegexLogLine> logItemWriter
    ) {
        return new StepBuilder("regexFlatFileStep", jobRepository)
            .<RegexLogLine, RegexLogLine>chunk(10, transactionManager)
            .reader(logItemReader)
            .writer(logItemWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<RegexLogLine> logItemReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        var tokenizer = new RegexLineTokenizer();
        tokenizer.setRegex("\\[\\w+\\]\\[Thread-(\\d+)\\]\\[CPU: \\d+%\\] (.+)");
        
        return new FlatFileItemReaderBuilder<RegexLogLine>()
            .name("logItemReader")
            .resource(new FileSystemResource(inputFile))
            .lineTokenizer(tokenizer)
            // 주의!! targetType 설정 시 fieldSetMapper 무시
            .fieldSetMapper(
                it -> RegexLogLine.builder()
                    .threadNum(it.readString(0))
                    .message(it.readString(1))
                    .build()
            )
            .build();
    }
    
    @Bean
    public ItemWriter<RegexLogLine> logItemWriter() {
        return items -> {
            for (RegexLogLine item : items) {
                System.out.println("THD-%s: %s".formatted(item.getThreadNum(), item.getMessage()));
            }
        };
    }
}
