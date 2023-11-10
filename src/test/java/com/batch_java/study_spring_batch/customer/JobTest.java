package com.batch_java.study_spring_batch.customer;

import com.batch_java.study_spring_batch.application.business.DormantBatchJobExecutionListener;
import com.batch_java.study_spring_batch.batch.business.Job;
import com.batch_java.study_spring_batch.batch.business.TaskletJob;
import com.batch_java.study_spring_batch.batch.enums.BatchStatus;
import com.batch_java.study_spring_batch.batch.model.JobExecution;
import com.batch_java.study_spring_batch.customer.entity.CustomerEntity;
import com.batch_java.study_spring_batch.customer.enums.CustomerStatus;
import com.batch_java.study_spring_batch.email.EmailSender;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
class JobTest {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private Job job;
    
    @Test
    @DisplayName("1년 이상 로그인 하지 않은 고객이 3명 있을 때, 해당하는 3명의 고객을 휴면으로 전환")
    void test1() {
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(364);
        saveCustomer(364);
        
        final JobExecution result = job.execute();
        
        final Long countDormant = customerRepository.findAll()
            .stream()
            .filter(it -> it.getStatus() == CustomerStatus.DORMANT)
            .count();
        
        Assertions.assertThat(countDormant).isEqualTo(3);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
    
    @Test
    @DisplayName("1년 이상 로그인 하지 않은 고객이 3명 있을 때, 해당하는 3명의 고객을 휴면으로 전환")
    void test2() {
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);
        saveCustomer(366);
        
        final JobExecution result = job.execute();
        
        final Long countDormant = customerRepository.findAll()
            .stream()
            .filter(it -> it.getStatus() == CustomerStatus.DORMANT)
            .count();
        
        Assertions.assertThat(countDormant).isEqualTo(8);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
    
    @Test
    @DisplayName("고객이 없는 경우에도 배치는 정상 동작")
    void test3() {
        final JobExecution result = job.execute();
        
        final Long countDormant = customerRepository.findAll()
            .stream()
            .filter(it -> it.getStatus() == CustomerStatus.DORMANT)
            .count();
        
        Assertions.assertThat(countDormant).isEqualTo(8);
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
    
    @Test
    @DisplayName("배치가 실패하면 Batch Status는 FAILED를 반환")
    void test4() {
        final Job job = new TaskletJob(null, new DormantBatchJobExecutionListener(new EmailSender()));
        
        final JobExecution result = job.execute();
        
        Assertions.assertThat(result.getStatus()).isEqualTo(BatchStatus.FAILED);
    }
    
    private void saveCustomer(Integer loginMinusDays) {
        final String uuid = UUID.randomUUID().toString();
        final CustomerEntity customerEntity = new CustomerEntity(uuid, uuid + "1@test.com");
        customerEntity.setLoginAt(LocalDateTime.now().minusDays(loginMinusDays));
        customerRepository.save(customerEntity);
    }
}