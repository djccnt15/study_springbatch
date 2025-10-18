package com.djccnt15.study_springbatch.batch.basic;

import com.djccnt15.study_springbatch.batch.basic.chunk.ChunkItemProcessor;
import com.djccnt15.study_springbatch.batch.basic.chunk.ChunkItemReader;
import com.djccnt15.study_springbatch.batch.basic.chunk.ChunkItemWriter;
import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

// `Chunk` 지향 처리. 데이터를 일정 청크로 나누어 Read/Process/Write 처리 -> 청크 단위 반복
// 메모리 절약, 장애 시 에러 발생한 청크만 롤백 후 해당 청크부터 재시작 가능
@Slf4j
@Batch
@RequiredArgsConstructor
public class ChunkBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job chunkOrientedJob() {
        return new JobBuilder("chunkOrientedJob", jobRepository)
            .start(chunkOrientedStep())  // Step 등록
            .build();
    }
    
    @Bean
    public Step chunkOrientedStep() {
        return new StepBuilder("chunkOrientedStep", jobRepository)
            .<Integer, String>chunk(10, transactionManager)  // 청크 지향 처리 활성화, 청크 사이즈 지정
            // `chunk` 메서드의 제너릭을 통해 처리할 item의 타입(ItemReader가 반환할 타입)과
            // 처리된 item의 타입(ItemProcessor가 반환할 타입, ItemWriter가 입력 받을 타입)을 지정
            .reader(chunkItemReader())  // 데이터 읽기 담당
            .processor(chunkItemProcessor())  // 데이터 처리 담당. 생략 가능
            .writer(chunkItemWriter())  // 데이터 쓰기 담당
            .build();
    }
    
    @Bean
    public ItemReader<Integer> chunkItemReader() {
        return new ChunkItemReader();
    }
    
    @Bean
    public ItemProcessor<Integer, String> chunkItemProcessor() {
        return new ChunkItemProcessor();
    }
    
    @Bean
    public ItemWriter<String> chunkItemWriter() {
        return new ChunkItemWriter();
    }
    
}
