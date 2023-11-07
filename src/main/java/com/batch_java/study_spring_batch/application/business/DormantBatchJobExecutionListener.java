package com.batch_java.study_spring_batch.application.business;

import com.batch_java.study_spring_batch.batch.business.JobExecutionListener;
import com.batch_java.study_spring_batch.batch.model.JobExecution;
import com.batch_java.study_spring_batch.email.EmailSender;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DormantBatchJobExecutionListener implements JobExecutionListener {
    
    private final EmailSender emailSender;
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        // no operation
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        emailSender.send(
            "admin@test.com",
            "배치 완료 알림",
            String.format(
                "DormantBatchJob이 수행됨\nstatus: %s",
                jobExecution.getStatus()
            )
        );
    }
    
    @Override
    public void afterJob(JobExecution jobExecution, String message) {
        emailSender.send(
            "admin@test.com",
            "배치 완료 알림",
            String.format(
                "DormantBatchJob이 수행됨\nstatus: %s\nmessage: %s",
                jobExecution.getStatus(),
                message
            )
        );
    }
}
