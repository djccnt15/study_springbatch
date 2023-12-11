package com.batch_java.study_spring_batch.settlement_batch.group;

import com.batch_java.study_spring_batch.domain.repository.CustomerRepository;
import com.batch_java.study_spring_batch.settlement_batch.group.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class SettleGroupReader implements ItemReader<Customer> {
    
    private final CustomerRepository customerRepository;
    private Iterator<Customer> customerIterator;
    private int pageNo = 0;
    
    public SettleGroupReader() {
        this.customerRepository = new CustomerRepository.Fake();
        customerIterator = Collections.emptyIterator();
    }
    
    @Override
    public Customer read() {
        
        if (customerIterator.hasNext()) {
            return customerIterator.next();
        }
        customerIterator = customerRepository.findAll(PageRequest.of(pageNo++, 10)).iterator();
        
        if (!customerIterator.hasNext()) {
            return null;
        }
        return customerIterator.next();
    }
}
