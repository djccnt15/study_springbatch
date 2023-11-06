package com.batch_java.study_spring_batch.email;

public interface EmailProvider {

    void send(String emailAddress, String title, String body);
}
