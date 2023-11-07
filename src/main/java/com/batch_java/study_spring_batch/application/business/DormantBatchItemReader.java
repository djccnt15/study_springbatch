package com.batch_java.study_spring_batch.application.business;

import com.batch_java.study_spring_batch.batch.business.ItemReader;
import com.batch_java.study_spring_batch.customer.CustomerRepository;
import com.batch_java.study_spring_batch.customer.entity.CustomerEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DormantBatchItemReader implements ItemReader<CustomerEntity> {
    
    private final CustomerRepository customerRepository;
    private int pageNo = 0;
    
    @Override
    public CustomerEntity read() {
        
        final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending());
        final Page<CustomerEntity> page = customerRepository.findAll(pageRequest);
        final CustomerEntity customerEntity;
        
        if (page.isEmpty()) {
            return null;
        } else {
            pageNo ++;
            return page.getContent().get(0);
        }
    }
}
