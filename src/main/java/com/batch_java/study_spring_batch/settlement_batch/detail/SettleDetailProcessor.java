package com.batch_java.study_spring_batch.settlement_batch.detail;

import com.batch_java.study_spring_batch.domain.ServicePolicy;
import com.batch_java.study_spring_batch.model.SettleDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class SettleDetailProcessor implements ItemProcessor<KeyAndCount, SettleDetail>, StepExecutionListener {
    
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private StepExecution stepExecution;
    
    @Override
    public SettleDetail process(KeyAndCount item) throws Exception {
        final Key key = item.key();
        final ServicePolicy servicePolicy = ServicePolicy.findById(key.serviceId());
        final Long count = item.count();
        
        final String targetDate = stepExecution.getJobParameters().getString("targetDate");
        
        return SettleDetail.builder()
            .customerId(key.customerId())
            .serviceId(key.serviceId())
            .count(count)
            .fee(servicePolicy.getFee() * count)
            .targetDate(LocalDate.parse(targetDate, dateTimeFormatter))
            .build();
    }
    
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
}
