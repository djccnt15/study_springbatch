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
public class TolerantSkipProcessorBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ItemReader<Scream> tolerantRetryReader;
    private final SkipListener<Scream, Scream> tolerantSkipListener;
    
    @Bean
    public Job tolerantSkipProcessJob() {
        return new JobBuilder("tolerantSkipProcessJob", jobRepository)
            .start(tolerantSkipProcessStep())
            .build();
    }
    
    @Bean
    public Step tolerantSkipProcessStep() {
        return new StepBuilder("tolerantSkipProcessStep", jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(tolerantRetryReader)
            .processor(tolerantSkipProcessProcessor())
            .writer(tolerantSkipProcessWriter())
            .faultTolerant()  // 내결함성 기능 ON
            .skip(TerminationFailedException.class)  // 재시도 대상 예외 추가
            .skipLimit(2)
            .listener(tolerantSkipListener)
            .processorNonTransactional()  // 청크 비트랜잭션 처리 및 캐시 사용, 처리된 아이템에 대한 불필요한 재시도 방지
            .build();
    }
    
    @Bean
    public ItemProcessor<Scream, Scream> tolerantSkipProcessProcessor() {
        return scream -> {
            System.out.print("[ItemProcessor]: 처형 대상 = " + scream);
            
            if (scream.getId() == 2 || scream.getId() == 5) {
                System.out.println(" -> 처형 실패.");
                throw new TerminationFailedException("처형 거부자 = " + scream);
            } else {
                System.out.println(" -> 처형 완료(" + scream.getProcessMsg() + ")");
            }
            
            return scream;
        };
    }
    
    @Bean
    public ItemWriter<Scream> tolerantSkipProcessWriter() {
        return items -> {
            System.out.println("[ItemWriter]: 처형 기록 시작. 기록 대상 = " + items.getItems());
            
            for (Scream scream : items) {
                System.out.println("[ItemWriter]: 기록 완료. 처형된 아이템 = " + scream);
            }
        };
    }
}
