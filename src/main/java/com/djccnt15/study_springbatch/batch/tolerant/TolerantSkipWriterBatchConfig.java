package com.djccnt15.study_springbatch.batch.tolerant;

import com.djccnt15.study_springbatch.annotation.Batch;
import com.djccnt15.study_springbatch.batch.tolerant.model.Scream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantSkipWriterBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ItemReader<Scream> tolerantRetryReader;
    private final SkipListener<Scream, Scream> tolerantSkipListener;
    
    @Bean
    public Job tolerantSkipWriterJob() {
        return new JobBuilder("tolerantSkipWriterJob", jobRepository)
            .start(tolerantSkipWriterStep())
            .build();
    }
    
    @Bean
    public Step tolerantSkipWriterStep() {
        return new StepBuilder("tolerantSkipWriterStep", jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(tolerantRetryReader)
            .processor(tolerantSkipWriterProcessor())
            .writer(tolerantSkipWriteWriter())
            .faultTolerant()  // 내결함성 기능 ON
            .skip(TerminationFailedException.class)  // 재시도 대상 예외 추가
            .skipLimit(2)
            .listener(tolerantSkipListener)
            .processorNonTransactional()  // 청크 비트랜잭션 처리 및 캐시 사용, 처리된 아이템에 대한 불필요한 재시도 방지
            .build();
    }
    
    @Bean
    public ItemProcessor<Scream, Scream> tolerantSkipWriterProcessor() {
        return scream -> {
            System.out.print("[ItemProcessor]: 처형 대상 = " + scream + "\n");
            return scream;
        };
    }
    
    // Writer에서 오류 발생 시 scan 모드로 전환 후 단일 아이템 1개씩 처리
    // Writer skip 발생 시 성능 효율이 급감하기 때문에 데이터 품질 등 사전에 방지 가능한 케이스는 최대한 방지 필요
    @Bean
    public ItemWriter<Scream> tolerantSkipWriteWriter() {
        return screams -> {
            System.out.println("[ItemWriter]: 기록 시작. 처형된 아이템들 = " + screams);
            
            for (Scream scream : screams) {
                if (scream.getId() == 2 || scream.getId() == 5) {
                    System.out.println("[ItemWriter]: 기록 실패. 저항하는 아이템 발견 = " + scream);
                    throw new TerminationFailedException("기록 거부자 = " + scream);
                }
                System.out.println("[ItemWriter]: 기록 완료. 처형된 아이템 = " + scream);
            }
        };
    }
}
