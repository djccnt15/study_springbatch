package com.batch_java.study_spring_batch.settlement_batch.sample_data_generator;

import com.batch_java.study_spring_batch.model.ApiOrder;
import com.batch_java.study_spring_batch.domain.ServicePolicy;
import com.batch_java.study_spring_batch.enums.State;
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
public class ApiOrderGenerateProcessor implements ItemProcessor<String, ApiOrder> {
    
    private final List<Long> customerIds = LongStream.range(0, 20).boxed().toList();
    private final List<ServicePolicy> servicePolicies = Arrays.stream(ServicePolicy.values()).toList();
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    
    @Override
    public ApiOrder process(
        String item
    ) throws Exception {
        
        final Long randomCustomerId = customerIds.get(random.nextInt(customerIds.size()));
        final ServicePolicy randomServicePolicy = servicePolicies.get(random.nextInt(servicePolicies.size()));
        final State state = random.nextInt(10) % 10 == 1 ? State.FAIL : State.SUCCESS;
        
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        final LocalDateTime requestedAt = LocalDateTime.of(LocalDate.parse(item, formatter), LocalTime.now());
        
        return ApiOrder.builder()
            .id(UUID.randomUUID().toString())
            .customerId(randomCustomerId)
            .url(randomServicePolicy.getUrl())
            .state(state)
            .createdAt(LocalDateTime.now())
            .requestedAt(requestedAt)
            .build();
    }
}
