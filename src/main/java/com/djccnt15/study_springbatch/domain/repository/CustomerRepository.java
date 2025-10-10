package com.djccnt15.study_springbatch.domain.repository;


import com.djccnt15.study_springbatch.settlement_batch.group.model.Customer;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public interface CustomerRepository {
    
    List<Customer> findAll(Pageable pageable);
    
    Customer findById(Long id);
    
    class Fake implements CustomerRepository {
        
        @Override
        public List<Customer> findAll(Pageable pageable) {
            
            if (pageable.getPageNumber() == 0) {
                return List.of(
                    new Customer(0L, "Aco", "A@company.com"),
                    new Customer(1L, "Bco", "B@company.com"),
                    new Customer(2L, "Cco", "C@company.com"),
                    new Customer(3L, "Dco", "D@company.com"),
                    new Customer(4L, "Eco", "E@company.com"),
                    new Customer(5L, "Fco", "F@company.com"),
                    new Customer(6L, "Gco", "G@company.com"),
                    new Customer(7L, "Hco", "H@company.com"),
                    new Customer(8L, "Ico", "I@company.com"),
                    new Customer(9L, "Jco", "J@company.com")
                );
            } else if (pageable.getPageNumber() == 1) {
                return List.of(
                    new Customer(10L, "Kco", "K@company.com"),
                    new Customer(11L, "Lco", "L@company.com"),
                    new Customer(12L, "Mco", "M@company.com"),
                    new Customer(13L, "Nco", "N@company.com"),
                    new Customer(14L, "Oco", "O@company.com"),
                    new Customer(15L, "Pco", "P@company.com"),
                    new Customer(16L, "Qco", "Q@company.com"),
                    new Customer(17L, "Rco", "R@company.com"),
                    new Customer(18L, "Sco", "S@company.com"),
                    new Customer(19L, "Tco", "T@company.com")
                );
            } else {
                return Collections.emptyList();
            }
        }
        
        @Override
        public Customer findById(Long id) {
            return Stream.of(
                new Customer(0L, "Aco", "A@company.com"),
                new Customer(1L, "Bco", "B@company.com"),
                new Customer(2L, "Cco", "C@company.com"),
                new Customer(3L, "Dco", "D@company.com"),
                new Customer(4L, "Eco", "E@company.com"),
                new Customer(5L, "Fco", "F@company.com"),
                new Customer(6L, "Gco", "G@company.com"),
                new Customer(7L, "Hco", "H@company.com"),
                new Customer(8L, "Ico", "I@company.com"),
                new Customer(9L, "Jco", "J@company.com"),
                new Customer(10L, "Kco", "K@company.com"),
                new Customer(11L, "Lco", "L@company.com"),
                new Customer(12L, "Mco", "M@company.com"),
                new Customer(13L, "Nco", "N@company.com"),
                new Customer(14L, "Oco", "O@company.com"),
                new Customer(15L, "Pco", "P@company.com"),
                new Customer(16L, "Qco", "Q@company.com"),
                new Customer(17L, "Rco", "R@company.com"),
                new Customer(18L, "Sco", "S@company.com"),
                new Customer(19L, "Tco", "T@company.com")
            ).filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow();
        }
    }
}
