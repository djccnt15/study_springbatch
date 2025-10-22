package com.djccnt15.study_springbatch.batch.listener.listener.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobExecutionListenerV2 {
    
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("beforeJob, jobExecution: {}", jobExecution.getStatus());
    }
    
    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        log.info("afterJob, jobExecution: {}", jobExecution.getStatus());
    }
}
