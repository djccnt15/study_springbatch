package com.djccnt15.study_springbatch.batch.flatfile;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.model.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
public class FlatFileItemBatchConfig {
    
    @Bean
    public Job flatFileItemJob(
        JobRepository jobRepository,
        Step flatFileItemStep
    ) {
        return new JobBuilder("flatFileItemJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(flatFileItemStep)
            .build();
    }
    
    @Bean
    public Step flatFileItemStep(
        JobRepository jobRepository,
        PlatformTransactionManager transactionManager,
        ItemReader<UserEntity> flatFileItemReader,
        ItemWriter<UserEntity> formattedFlatFileItemWriter
    ) {
        return new StepBuilder("flatFileItemStep", jobRepository)
            .<UserEntity, UserEntity>chunk(2, transactionManager)
            .reader(flatFileItemReader)
            .writer(formattedFlatFileItemWriter)
            .build();
    }
    
    @Bean
    @StepScope
    public FlatFileItemReader<UserEntity> flatFileItemReader(
        @Value("#{jobParameters['inputFile']}") String inputFile
    ) {
        return new FlatFileItemReaderBuilder<UserEntity>()
            .name("flatFileItemReader")
            .resource(new FileSystemResource(inputFile))
            .linesToSkip(1)
            .delimited().delimiter(",")
            .names("name", "age", "region", "phoneNumber")
            .targetType(UserEntity.class)
            .strict(true)
            .build();
    }
    
    @Bean
    @StepScope
    public ItemWriter<UserEntity> formattedFlatFileItemWriter(
        @Value("#{jobParameters['targetFile']}") String targetFile
    ) {
        return new FlatFileItemWriterBuilder<UserEntity>()
            .name("flatFileItemWriter")
            .resource(new PathResource(targetFile))
            .formatted().format("name: %s, age: %s, region: %s, phoneNumber: %s")
            .names("name", "age", "region", "phoneNumber")
            .shouldDeleteIfEmpty(true)  // 헤더와 푸터를 제외한 데이터가 하나도 쓰여지지 않았을 경우 삭제 여부
            // 빈 파일 여부는 현재 스텝에서 FlatFileItemWriter가 실제로 쓴 라인 수를 기준으로 결정
            // 현재 스텝에서 아무 데이터도 쓰지 않았을 경우 기존에 데이터가 있던 파일까지 삭제될 수 있으니 주의
            .shouldDeleteIfExists(true)  // 초기화 시점에 기존 파일이 있다면 삭제하고 새로 파일을 생성
            .append(true)  // 덧붙여 작성. shouldDeleteIfExists false 처리
            // .forceSync(true)  // 캐시 없이 디스크에 바로 파일 반영하도록 강제. 미설정 시 성능을 위해 메모리 캐시 사용
            .build();
    }
}
