package com.batch_java.study_spring_batch.email;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailSender implements EmailProvider {
    
    @Override
    public void send(String emailAddress, String title, String body) {
        log.info("{} send email complete {}: {}", emailAddress, title, body);
    }
}
