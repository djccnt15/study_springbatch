package com.batch_java.study_spring_batch.batch.business;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
public class Step {
    
    private final Tasklet tasklet;
    
    public void execute() {
        tasklet.execute();
    }
    
    @Builder
    public Step(
        ItemReader<?> itemReader,
        ItemProcessor<?, ?> itemProcessor,
        ItemWriter<?> itemWriter
    ) {
        this.tasklet = new SimpleTasklet(itemReader, itemProcessor, itemWriter);
    }
}
