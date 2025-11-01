package com.djccnt15.study_springbatch.batch.jobexecution.tasklet;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobExecutionTasklet implements Tasklet {
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        var jobParameters = chunkContext.getStepContext()
            .getStepExecution()
            .getJobParameters();
        
        var target = jobParameters.getString("target");
        var level = jobParameters.getLong("jobParam.level");
        
        log.info("target: {}", target);
        log.info("level: {}", level);
        
        return RepeatStatus.FINISHED;
    }
}
