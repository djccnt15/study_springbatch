package com.djccnt15.study_springbatch.batch.listener.listener.v2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepExecutionListenerV2 {
    
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("beforeStep, stepExecution: {}", stepExecution.getStepName());
    }
    
    @AfterStep
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("afterStep, stepExecution: {}", stepExecution.getStatus());
        return ExitStatus.COMPLETED;
    }
}
