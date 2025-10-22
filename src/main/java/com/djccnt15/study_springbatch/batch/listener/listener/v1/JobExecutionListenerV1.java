package com.djccnt15.study_springbatch.batch.listener.listener.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobExecutionListenerV1 implements JobExecutionListener {
    
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("beforeJob, jobExecution: {}", jobExecution.getStatus());
    }
    
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("afterJob, jobExecution: {}", jobExecution.getStatus());
    }
}
