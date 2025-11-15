package com.djccnt15.study_springbatch.batch.tolerant;

import com.djccnt15.study_springbatch.batch.tolerant.model.Scream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

import java.util.List;

@Slf4j
@Configuration
public class TolerantConfig {
    
    @Bean
    public RetryListener tolerantRetryListener() {
        return new RetryListener() {
            @Override
            public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                System.out.println("종료 실패 " + throwable + " (현재 총 시도 횟수=" + context.getRetryCount() + "). 재처리.\n");
            }
        };
    }
    
    @Bean
    public SkipListener<Scream, Scream> tolerantSkipListener() {
        return new SkipListener<>() {
            @Override
            public void onSkipInRead(Throwable t) {
                System.out.println("[onSkipInRead] 처형 불가 판정! 불가 원인 = " + t.getMessage());
            }
            
            @Override
            public void onSkipInProcess(Scream scream, Throwable t) {
                System.out.println("[onSkipInProcess] 처형 불가 판정! 생존자: [" + scream.getId() + "]. 저항 패턴 = " + t.getMessage());
            }
            
            @Override
            public void onSkipInWrite(Scream scream, Throwable t) {
                System.out.println("[onSkipInWrite] 처형 불가 판정! 생존자: [" + scream.getId() + "]. 저항 패턴 = " + t.getMessage());
            }
        };
    }
    
    @Bean
    public ListItemReader<Scream> tolerantRetryReader() {
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
                var scream = super.read();
                if (scream == null) {
                    return null;
                }
                System.out.println("[ItemReader]: 처형 대상 = " + scream);
                return scream;
            }
        };
    }
}
