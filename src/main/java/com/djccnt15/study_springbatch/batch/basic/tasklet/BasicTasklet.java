package com.djccnt15.study_springbatch.batch.basic.tasklet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class BasicTasklet implements Tasklet {
    private final int goal = 10;
    private int count = 0;
    
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        count++;
        log.info("tasklet finished - ({}/{})", count, goal);
        
        if (count >= goal) {
            log.info("tasklet finished");
            return RepeatStatus.FINISHED;  // Step 완료
        }
        
        log.info("tasklet continuable");
        return RepeatStatus.CONTINUABLE;  // Step 추가 실행
    }
}
