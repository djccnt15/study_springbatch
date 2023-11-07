package com.batch_java.study_spring_batch.batch.business;

import com.batch_java.study_spring_batch.batch.model.JobExecution;

public interface JobExecutionListener {

    void beforeJob(JobExecution jobExecution);
    
    void afterJob(JobExecution jobExecution);
    
    void afterJob(JobExecution jobExecution, String message);
}
