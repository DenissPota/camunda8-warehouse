package com.example.camunda8warehouse.workers.notification;

import com.example.camunda8warehouse.api.model.OrderPost;
import com.example.camunda8warehouse.services.email.EmailService;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationWorker {

    private final EmailService emailService;

    @JobWorker(type = "send-email")
    public void sendEmail(@Variable OrderPost order) {
        emailService.sendEmail("Order not shipped due to invalid input!", order.toString(), "test@example.com");
    }
}
