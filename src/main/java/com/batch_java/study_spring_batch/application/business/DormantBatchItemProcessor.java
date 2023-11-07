package com.batch_java.study_spring_batch.application.business;

import com.batch_java.study_spring_batch.batch.business.ItemProcessor;
import com.batch_java.study_spring_batch.customer.entity.CustomerEntity;
import com.batch_java.study_spring_batch.customer.enums.CustomerStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DormantBatchItemProcessor implements ItemProcessor<CustomerEntity, CustomerEntity> {
    
    @Override
    public CustomerEntity process(CustomerEntity item) {
        final boolean isDormantTarget = LocalDate.now()
            .minusDays(365)
            .isAfter(item.getLoginAt().toLocalDate());

        if (isDormantTarget) {
            item.setStatus(CustomerStatus.DORMANT);
            return item;
        } else {
            return null;
        }
    }
}
