package com.djccnt15.study_springbatch.batch.tolerant;

import com.djccnt15.study_springbatch.annotation.Batch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantSkipBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    
    @Bean
    public Job tolerantSkipItemJob() {
        return new JobBuilder("tolerantSkipItemJob", jobRepository)
            .start(tolerantSkipItemStep())
            .build();
    }
    
    @Bean
    public Step tolerantSkipItemStep() {
        // 비즈니스적으로 중요도가 낮은 데이터를 처리할 때
        // 일부 실패 레코드의 처리보다 전체 배치 프로세스 완료가 더 중요할 때
        // 입력 데이터의 품질이 균일하지 않을 때
        // LimitCheckingItemSkipPolicy
        return new StepBuilder("tolerantSkipItemStep", jobRepository)
            .chunk(10, transactionManager)
            .reader(tolerantSkipItemReader())
            // .processor()
            .writer(read -> {})
            .faultTolerant()
            .skip(IllegalStateException.class)
            // LimitCheckingItemSkipPolicy와 커스텀 SkipPolicy를 같이 적용할 경우 OR 조건으로 작동해 둘 중 하나라도 skip 가능하다고 판단하면 해당 아이템은 skip
            // .skipLimit(10)  // LimitCheckingItemSkipPolicy
            .skipPolicy(    // custom SkipPolicy
                (t, skipCount) -> t instanceof IllegalStateException && skipCount < 3
            )
            .build();
    }
    
    @Bean
    public ItemReader<Integer> tolerantSkipItemReader() {
        return new ItemReader<>() {
            private int count = 0;
            
            @Override
            public Integer read() {
                count++;
                
                log.info("Read {}", count);
                
                if (count >= 15) {
                    throw new IllegalStateException("Exception raised");
                }
                
                return count;
            }
        };
    }
}
