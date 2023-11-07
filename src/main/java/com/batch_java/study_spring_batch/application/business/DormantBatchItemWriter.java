package com.batch_java.study_spring_batch.application.business;

import com.batch_java.study_spring_batch.batch.business.ItemWriter;
import com.batch_java.study_spring_batch.customer.CustomerRepository;
import com.batch_java.study_spring_batch.customer.entity.CustomerEntity;
import com.batch_java.study_spring_batch.email.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DormantBatchItemWriter implements ItemWriter<CustomerEntity> {
    
    private final CustomerRepository customerRepository;
    private final EmailSender emailSender;
    
    @Override
    public void write(CustomerEntity item) {
        customerRepository.save(item);
        emailSender.send(item.getEmail(), "휴면 전환 안내 메일", "내용");
    }
}
