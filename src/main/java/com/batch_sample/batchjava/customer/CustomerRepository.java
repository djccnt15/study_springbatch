package com.batch_sample.batchjava.customer;

import com.batch_sample.batchjava.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
