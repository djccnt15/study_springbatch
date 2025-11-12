package com.djccnt15.study_springbatch.batch.tolerant;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.tolerant.model.Scream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RetryListener;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantRetryWriterBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RetryListener tolerantRetryListener;
    private final ItemReader<Scream> tolerantRetryReader;
    
    @Bean
    public Job tolerantRetryWriterJob() {
        return new JobBuilder("tolerantRetryWriterJob", jobRepository)
            .start(tolerantRetryWriterStep())
            .build();
    }
    
    @Bean
    public Step tolerantRetryWriterStep() {
        return new StepBuilder("tolerantRetryWriterStep", jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(tolerantRetryReader)
            .processor(tolerantRetryWriterProcessor())
            .writer(tolerantRetryWriteWriter())
            .faultTolerant()  // 내결함성 기능 ON
            .retry(TerminationFailedException.class)  // 재시도 대상 예외 추가
            .retry(IllegalStateException.class)
            .retryLimit(3)  // 청크 전체가 재처리
            .listener(tolerantRetryListener)
            .build();
    }
    
    @Bean
    public ItemProcessor<Scream, Scream> tolerantRetryWriterProcessor() {
        return scream -> {
            System.out.print("[ItemProcessor]: 처형 대상 = " + scream + "\n");
            return scream;
        };
    }
    
    // ItemWriter에서 예외 발생 시 ItemProcessor부터 재처리. ItemWriter에서의 재시도 횟수는 청크 단위로 관리
    @Bean
    public ItemWriter<Scream> tolerantRetryWriteWriter() {
        return new ItemWriter<>() {
            private static final int MAX_PATIENCE = 3;
            private int mercy = 0;  // 자비 카운트
            
            @Override
            public void write(Chunk<? extends Scream> screams) {
                System.out.println("[ItemWriter]: 기록 시작. 처형된 아이템들 = " + screams);
                mercy ++;
                
                for (Scream scream : screams) {
                    if (scream.getId() == 3 && mercy < MAX_PATIENCE) {
                        System.out.println("[ItemWriter]: 기록 실패. 저항하는 아이템 발견 = " + scream);
                        throw new TerminationFailedException("기록 거부자 = " + scream);
                    }
                    System.out.println("[ItemWriter]: 기록 완료. 처형된 아이템 = " + scream);
                }
            }
        };
    }
}
