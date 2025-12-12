package com.djccnt15.study_springbatch.batch.practice.settlement.utils;

import lombok.extern.slf4j.Slf4j;

public interface EmailProvider {
    
    void send(String emailAddress, String title, String body);
    
    @Slf4j
    class Fake implements EmailProvider {
        @Override
        public void send(String emailAddress, String title, String body) {
            log.info("Send Email Complete\nAddress: {}\nTitle: {}\nBody: {}", emailAddress, title, body);
        }
    }
}
