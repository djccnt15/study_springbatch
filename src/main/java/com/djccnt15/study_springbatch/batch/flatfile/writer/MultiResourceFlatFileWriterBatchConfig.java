package com.djccnt15.study_springbatch.batch.flatfile.writer;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.flatfile.writer.model.FlatFileItemWriteModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Slf4j
@Batch
public class MultiResourceFlatFileWriterBatchConfig {
    
    @Bean
    public Job multiResourceItemWriteJob(
        JobRepository jobRepository,
        Step multiResourceItemWriteStep
    ) {
        return new JobBuilder("multiResourceItemWriteJob", jobRepository)
            .start(multiResourceItemWriteStep)
            .build();
    }
    
    @Bean
    public Step multiResourceItemWriteStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ListItemReader<FlatFileItemWriteModel> multiResourceFlatFileWriterReader,
        MultiResourceItemWriter<FlatFileItemWriteModel> multiResourceFlatFileItemWriter
    ) {
        return new StepBuilder("multiResourceItemWriteStep", jobRepository)
            .<FlatFileItemWriteModel, FlatFileItemWriteModel>chunk(10, transactionManager)
            .reader(multiResourceFlatFileWriterReader)
            .writer(multiResourceFlatFileItemWriter)
            .build();
    }
    
    @Bean
    public ListItemReader<FlatFileItemWriteModel> multiResourceFlatFileWriterReader() {
        var deathNotes = new ArrayList<FlatFileItemWriteModel>();
        for (int i = 1; i <= 15; i++) { // 총 15개의 DeathNote 객체 read()
            var id = "KILL-%03d".formatted(i);
            var date = LocalDate.now().plusDays(i);
            deathNotes.add(new FlatFileItemWriteModel(
                id,
                "대상" + i,
                date.format(DateTimeFormatter.ISO_DATE),
                "원인" + i
            ));
        }
        return new ListItemReader<>(deathNotes);
    }
    
    // 개별 파일의 write를 담당할 실제 writer
    @Bean
    public FlatFileItemWriter<FlatFileItemWriteModel> delegateItemWriter() {
        return new FlatFileItemWriterBuilder<FlatFileItemWriteModel>()
            .name("delegateItemWriter")
            .formatted()
            .format("ID: %s | 일자: %s | 대상: %s | 원인: %s")  // custom 포멧 지정
            .sourceType(FlatFileItemWriteModel.class)
            .names("id", "date", "name", "cause")
            .headerCallback(it -> it.write("================= 실행 ================="))
            .footerCallback(it -> it.write("================= 완료 =================="))
            .build();
    }
    
    // multi resource write를 관리하는 writer
    @Bean
    @StepScope
    public MultiResourceItemWriter<FlatFileItemWriteModel> multiResourceFlatFileItemWriter(
        @Value("#{jobParameters['outputDir']}") String outputDir
    ) {
        return new MultiResourceItemWriterBuilder<FlatFileItemWriteModel>()
            .name("multiResourceItemWriter")
            .resource(new FileSystemResource(outputDir + "/multiResource"))
            .itemCountLimitPerResource(10)  // 1개 파일 당 최대 라인 수
            .delegate(delegateItemWriter())
            .resourceSuffixCreator("_%03d.txt"::formatted)
            .build();
    }
}
