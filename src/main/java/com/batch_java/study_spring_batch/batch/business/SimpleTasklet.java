package com.batch_java.study_spring_batch.batch.business;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SimpleTasklet<I, O> implements Tasklet {
    
    private final ItemReader<I> itemReader;
    private final ItemProcessor<I, O> itemProcessor;
    private final ItemWriter<O> itemWriter;
    
    @Override
    public void execute() {
        
        while (true) {
            // select User
            final I read = itemReader.read();
            if (read == null) {
                break;
            }
            
            // extract Dormant target and change status
            final O process = itemProcessor.process(read);
            if (process == null) {
                continue;
            }
            
            // save changes and send email
            itemWriter.write(process);
        }
    }
}
