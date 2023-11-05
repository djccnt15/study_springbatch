package com.batch_sample.batchjava.customer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerDao, Long> {
}
