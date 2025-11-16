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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantRetryProcessorBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RetryListener tolerantRetryListener;
    private final ItemReader<Scream> tolerantRetryReader;
    
    @Bean
    public Job tolerantRetryProcessJob() {
        return new JobBuilder("tolerantRetryProcessJob", jobRepository)
            .start(tolerantRetryProcessStep())
            .build();
    }
    
    @Bean
    public Step tolerantRetryProcessStep() {
        return new StepBuilder("tolerantRetryProcessStep", jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(tolerantRetryReader)
            .processor(tolerantRetryProcessProcessor())
            .writer(tolerantRetryProcessWriter())
            .faultTolerant()  // 내결함성 기능 ON
            .retry(TerminationFailedException.class)  // 재시도 대상 예외 추가
            .retry(IllegalStateException.class)
            .retryLimit(3)  // 청크 전체가 재처리
            .listener(tolerantRetryListener)
            .processorNonTransactional()  // 청크 비트랜잭션 처리 및 캐시 사용, 처리된 아이템에 대한 불필요한 재시도 방지
            // ItemProcessor에서 예외가 발생하면 여전히 청크 단위의 트랜잭션은 롤백
            .build();
    }
    
    // ItemProcessor에서의 재시도 횟수는 아이템 단위로 개별 관리
    @Bean
    public ItemProcessor<Scream, Scream> tolerantRetryProcessProcessor() {
        return new ItemProcessor<>() {
            private static final int MAX_PATIENCE = 2;
            private int mercy = 0;  // 자비 카운트
            
            @Override
            public Scream process(Scream scream) throws Exception {
                System.out.print("[ItemProcessor]: 처형 대상 = " + scream);
                
                if (scream.getId() == 3 && mercy < MAX_PATIENCE) {
                    mercy ++;
                    System.out.println(" -> 처형 실패.");
                    throw new TerminationFailedException("처형 거부자 = " + scream);
                } else {
                    System.out.println(" -> 처형 완료(" + scream.getProcessMsg() + ")");
                }
                
                return scream;
            }
        };
    }
    
    @Bean
    public ItemWriter<Scream> tolerantRetryProcessWriter() {
        return items -> {
            System.out.println("[ItemWriter]: 처형 기록 시작. 기록 대상 = " + items.getItems());
            
            for (Scream scream : items) {
                System.out.println("[ItemWriter]: 기록 완료. 처형된 아이템 = " + scream);
            }
        };
    }
}
