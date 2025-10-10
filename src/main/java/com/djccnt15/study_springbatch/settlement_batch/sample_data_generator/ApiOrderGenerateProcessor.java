package com.djccnt15.study_springbatch.settlement_batch.sample_data_generator;

import com.djccnt15.study_springbatch.domain.ServicePolicy;
import com.djccnt15.study_springbatch.enums.State;
import com.djccnt15.study_springbatch.model.ApiOrderEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.LongStream;

@Slf4j
@Component
public class ApiOrderGenerateProcessor implements ItemProcessor<String, ApiOrderEntity> {
    
    private final List<Long> customerIds = LongStream.range(0, 20).boxed().toList();
    private final List<ServicePolicy> servicePolicies = Arrays.stream(ServicePolicy.values()).toList();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    
    @Override
    public ApiOrderEntity process(
        String item
    ) throws Exception {
        
        final Long randomCustomerId = customerIds.get(random.nextInt(customerIds.size()));
        final ServicePolicy randomServicePolicy = servicePolicies.get(random.nextInt(servicePolicies.size()));
        final State randomState = random.nextInt(10) % 10 == 1 ? State.FAIL : State.SUCCESS;
        
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final LocalDateTime requestedAt = LocalDateTime.of(LocalDate.parse(item, formatter), LocalTime.now());
        
        return ApiOrderEntity.builder()
            .id(UUID.randomUUID().toString())
            .customerId(randomCustomerId)
            .url(randomServicePolicy.getUrl())
            .state(randomState)
            .createdAt(LocalDateTime.now())
            .requestedAt(requestedAt)
            .build();
    }
}
