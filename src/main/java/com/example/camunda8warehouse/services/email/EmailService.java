package com.example.camunda8warehouse.services.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    public void sendEmail(String subject, String body, String recipient) {
        log.info("Sent email to {} with subject {} and body {}.", recipient, subject, body);
    }
}
