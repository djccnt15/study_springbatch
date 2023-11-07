package com.batch_java.study_spring_batch.batch.business;

public interface ItemWriter<O> {

    void write(O item);
}
