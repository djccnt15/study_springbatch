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
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpServerErrorException;

import java.net.http.HttpTimeoutException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantRetryPolicyBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RetryListener tolerantRetryListener;
    private final ItemReader<Scream> tolerantRetryReader;
    
    @Bean
    public Job tolerantRetryPolicyJob() {
        return new JobBuilder("tolerantRetryPolicyJob", jobRepository)
            .start(tolerantRetryPolicyStep())
            .build();
    }
    
    @Bean
    public Step tolerantRetryPolicyStep() {
        return new StepBuilder("tolerantRetryPolicyStep", jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(tolerantRetryReader)
            .processor(terminationRetryPolicyProcessor())
            .writer(terminationRetryPolicyWriter())
            .faultTolerant()
            // .retryPolicy(new TimeoutRetryPolicy(10L))  // 타임아웃 기반 정책
            // .retryPolicy(new MaxAttemptsRetryPolicy(10))  // 예외 구분 없이 재시도 횟수 기반 정책
            .retryPolicy(retryPolicy())
            // 여러 retry 정책을 동시에 적용하면 AND 조건으로 작동해 모두 재시도가 가능하다고 판단될때만 재시도 됨
            // 재시도 로직이 복잡해지기 때문에, 가능하면 retry(), retryLimit()을 사용자 정의 RetryPolicy와 동시에 사용하지 않는 것을 권장
            .backOffPolicy(new FixedBackOffPolicy() {{  // 재시도 간격 조정을 위한 BackOffPolicy
                setBackOffPeriod(1000); // 1초
            }})
            .backOffPolicy(new ExponentialBackOffPolicy() {{  // 재시도 간격 조정을 위한 BackOffPolicy
                setInitialInterval(1000L);  // 초기 대기 시간
                setMultiplier(2.0);         // 대기 시간 증가 배수
                setMaxInterval(10000L);     // 최대 대기 시간
            }})
            .listener(tolerantRetryListener)
            .build();
    }
    
    @Bean
    public ItemProcessor<Scream, Scream> terminationRetryPolicyProcessor() {
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
    public ItemWriter<Scream> terminationRetryPolicyWriter() {
        return items -> {
            System.out.println("[ItemWriter]: 처형 기록 시작. 기록 대상 = " + items.getItems());

            for (Scream scream : items) {
                System.out.println("[ItemWriter]: 기록 완료. 처형된 아이템 = " + scream);
            }
        };
    }
    
    private RetryPolicy retryPolicy() {
        var policyMap = new HashMap<Class<? extends Throwable>, RetryPolicy>();
        
        var basicRetryPolicy = new SimpleRetryPolicy(3, Map.of(TerminationFailedException.class, true));
        var dbRetryPolicy = new SimpleRetryPolicy(3, Map.of(TransientDataAccessException.class, true));
        var apiRetryPolicy = new SimpleRetryPolicy(5, Map.of(HttpServerErrorException.class, true));
        
        policyMap.put(TerminationFailedException.class, basicRetryPolicy);
        policyMap.put(DataAccessException.class, dbRetryPolicy);
        policyMap.put(HttpTimeoutException.class, apiRetryPolicy);
        
        var retryPolicy = new ExceptionClassifierRetryPolicy();
        retryPolicy.setPolicyMap(policyMap);
        
        return retryPolicy;
    }
}
