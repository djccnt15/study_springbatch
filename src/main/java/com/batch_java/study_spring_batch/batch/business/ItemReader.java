package com.batch_java.study_spring_batch.batch.business;

public interface ItemReader<T> {

    T read();
}
