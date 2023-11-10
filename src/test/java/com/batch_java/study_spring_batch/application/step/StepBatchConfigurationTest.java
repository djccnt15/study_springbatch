package com.batch_java.study_spring_batch.application.step;

import com.batch_java.study_spring_batch.batch.business.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StepBatchConfigurationTest {

    @Autowired
    private Job stepBatchJob;
    
    @Test
    void test() {
        stepBatchJob.execute();
    }
}