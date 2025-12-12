package com.djccnt15.study_springbatch.batch.practice.settlement.config.group;

import com.djccnt15.study_springbatch.batch.practice.settlement.model.Customer;
import com.djccnt15.study_springbatch.db.repository.CustomerRepository;
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
