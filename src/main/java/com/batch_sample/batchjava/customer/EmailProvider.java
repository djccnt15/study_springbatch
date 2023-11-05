package com.batch_sample.batchjava.customer;

public interface EmailProvider {

    void send(String emailAddress, String title, String body);
}
