package com.batch_java.study_spring_batch.batch.business;

public interface ItemProcessor<I, O> {
    
    O process(I item);
}
