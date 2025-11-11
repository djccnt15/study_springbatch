package com.djccnt15.study_springbatch.batch.tolerant;

public class TerminationFailedException extends RuntimeException {
    public TerminationFailedException(String message) {
        super(message);
    }
}
