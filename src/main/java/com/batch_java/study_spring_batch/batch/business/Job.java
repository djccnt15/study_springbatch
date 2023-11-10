package com.batch_java.study_spring_batch.batch.business;

import com.batch_java.study_spring_batch.batch.model.JobExecution;

public interface Job {
    JobExecution execute();
}
