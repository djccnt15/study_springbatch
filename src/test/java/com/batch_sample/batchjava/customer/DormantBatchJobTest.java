package com.batch_sample.batchjava.customer;

import com.batch_sample.batchjava.batch.DormantBatchJob;
import com.batch_sample.batchjava.customer.enums.CustomerStatus;
import com.batch_sample.batchjava.customer.entity.CustomerEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
class DormantBatchJobTest {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private DormantBatchJob dormantBatchJob;
    
    @Test
    @DisplayName("1년 이상 로그인 하지 않은 고객이 3명 있을 때, 해당하는 3명의 고객을 휴면으로 전환한다")
    void test1() {
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(364);
        saveCustomer(364);
        
        dormantBatchJob.execute();
        
        final Long countDormant = customerRepository.findAll()
            .stream()
            .filter(it -> it.getStatus() == CustomerStatus.DORMANT)
            .count();
        
        Assertions.assertThat(countDormant).isEqualTo(3);
    }
    
    @Test
    @DisplayName("고객이 없는 경우에도 배치는 정상 동작해야 한다")
    void test2() {
        dormantBatchJob.execute();
        
        final Long countDormant = customerRepository.findAll()
            .stream()
            .filter(it -> it.getStatus() == CustomerStatus.DORMANT)
            .count();
        
        Assertions.assertThat(countDormant).isEqualTo(3);
    }
    
    private void saveCustomer(Integer loginMinusDays) {
        final String uuid = UUID.randomUUID().toString();
        final CustomerEntity customerEntity = new CustomerEntity(uuid, uuid + "1@test.com");
        customerEntity.setLoginAt(LocalDateTime.now().minusDays(loginMinusDays));
        customerRepository.save(customerEntity);
    }
}