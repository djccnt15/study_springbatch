package com.djccnt15.study_springbatch.batch.tolerant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

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
}
