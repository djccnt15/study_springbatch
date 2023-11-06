package com.batch_java.study_spring_batch.batch;

import com.batch_java.study_spring_batch.batch.enums.BatchStatus;
import com.batch_java.study_spring_batch.batch.model.JobExecution;
import com.batch_java.study_spring_batch.customer.CustomerRepository;
import com.batch_java.study_spring_batch.customer.entity.CustomerEntity;
import com.batch_java.study_spring_batch.customer.enums.CustomerStatus;
import com.batch_java.study_spring_batch.email.EmailSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DormantBatchJob {

    private final CustomerRepository customerRepository;
    private final EmailSender emailSender;
    
    public DormantBatchJob(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        this.emailSender = new EmailSender();
    }
    
    public JobExecution execute() {
        
        var jobExecuteResult = JobExecution.builder()
            .status(BatchStatus.STARTING)
            .startTime(LocalDateTime.now())
            .build();
        
        int pageNo = 0;
        String error = "";
        
        try {
            
            while (true) {
                // select User
                final PageRequest pageRequest = PageRequest.of(pageNo, 1, Sort.by("id").ascending());
                final Page<CustomerEntity> page = customerRepository.findAll(pageRequest);
                final CustomerEntity customerEntity;
                
                if (page.isEmpty()) {
                    break;
                } else {
                    pageNo ++;
                    customerEntity = page.getContent().get(0);
                }
                
                // extract Dormant target and change status
                final boolean isDormantTarget = LocalDate.now()
                    .minusDays(365)
                    .isAfter(customerEntity.getLoginAt().toLocalDate());
                
                if (isDormantTarget) {
                    customerEntity.setStatus(CustomerStatus.DORMANT);
                }
                
                // save changes
                customerRepository.save(customerEntity);
                
                // send email
                emailSender.send(customerEntity.getEmail(), "휴면 전환 안내 메일", "내용");
            }
            
            jobExecuteResult.setStatus(BatchStatus.COMPLETED);
            
        } catch (Exception e) {
            jobExecuteResult.setStatus(BatchStatus.FAILED);
            error = e.toString();
        }
        
        jobExecuteResult.setEndTime(LocalDateTime.now());
        
        emailSender.send(
            "admin@test.com",
            "배치 완료 알림",
            "DormantBatchJob이 수행됨" + error
        );
        
        return jobExecuteResult;
    }
}
