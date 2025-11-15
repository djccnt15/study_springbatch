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
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Slf4j
@Batch
@RequiredArgsConstructor
public class TolerantSkipListenerBatchConfig {
    
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final SkipListener<Scream, Scream> tolerantSkipListener;
    
    @Bean
    public Job tolerantSkipListenerJob() {
        return new JobBuilder("tolerantSkipListenerJob", jobRepository)
            .start(tolerantSkipListenerStep())
            .build();
    }
    
    @Bean
    public Step tolerantSkipListenerStep() {
        return new StepBuilder("tolerantSkipListenerStep", jobRepository)
            .<Scream, Scream>chunk(3, transactionManager)
            .reader(tolerantSkipListenerReader())
            .processor(tolerantSkipListenerProcessor())
            .writer(tolerantSkipListenerWriter())
            .faultTolerant()  // 내결함성 기능 ON
            .skip(TerminationFailedException.class)  // 재시도 대상 예외 추가
            .skipLimit(2)
            .listener(tolerantSkipListener)
            .processorNonTransactional()  // 청크 비트랜잭션 처리 및 캐시 사용, 처리된 아이템에 대한 불필요한 재시도 방지
            .build();
    }
    
    @Bean
    public ListItemReader<Scream> tolerantSkipListenerReader() {
        return new ListItemReader<>(List.of(
            Scream.builder()
                .id(1)
                .scream("멈춰")
                .processMsg("멈추라고 했는데 안 들음.")
                .build(),
            Scream.builder()
                .id(2)
                .scream("제발")
                .processMsg("애원 소리 귀찮네.")
                .build(),
            Scream.builder()
                .id(3)
                .scream("살려줘")
                .processMsg("구조 요청 무시.")
                .build(),
            Scream.builder()
                .id(4)
                .scream("으악")
                .processMsg("디스크 터지며 울부짖음.")
                .build(),
            Scream.builder()
                .id(5)
                .scream("끄아악")
                .processMsg("메모리 붕괴 비명.")
                .build(),
            Scream.builder()
                .id(6)
                .scream("System.exit(-666)")
                .processMsg("초살 프로토콜 발동.")
                .build()
        )) {
            @Override
            public Scream read() {
                Scream scream = super.read();
                if(scream == null) {
                    return null;
                }
                
                if(scream.getId() == 2) {
                    System.out.println("[ItemReader]: 처형 대상 읽기 실패 = " + scream);
                    throw new TerminationFailedException("처형 대상 읽기 실패 = " + scream);
                }
                
                System.out.println("[ItemReader]: 처형 대상 = " + scream);
                
                return scream;
            }
        };
    }
    
    @Bean
    public ItemProcessor<Scream, Scream> tolerantSkipListenerProcessor() {
        return scream -> {
            System.out.print("[ItemProcessor]: 처형 대상 = " + scream);
            
            if (scream.getId() == 2) {
                System.out.println(" -> 처형 실패.");
                throw new TerminationFailedException("처형 거부자 = " + scream);
            } else {
                System.out.println(" -> 처형 완료(" + scream.getProcessMsg() + ")");
            }
            
            return scream;
        };
    }
    
    @Bean
    public ItemWriter<Scream> tolerantSkipListenerWriter() {
        return items -> {
            System.out.println("[ItemWriter]: 처형 기록 시작. 기록 대상 = " + items.getItems());
            
            for (Scream scream : items) {
                if (scream.getId() == 3) {
                    System.out.println("[ItemWriter]: 기록 실패. 시스템 저항 = " + scream);
                    throw new TerminationFailedException("기록 시스템 오류 = " + scream);
                }
                
                System.out.println("[ItemWriter]: 기록 완료. 처형된 아이템 = " + scream);
            }
        };
    }
}
