package com.batch_sample.batchjava.email;

public interface EmailProvider {

    void send(String emailAddress, String title, String body);
}
