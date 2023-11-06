package com.batch_java.study_spring_batch.customer;

import com.batch_java.study_spring_batch.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
