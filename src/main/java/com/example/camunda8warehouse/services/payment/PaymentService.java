package com.example.camunda8warehouse.services.payment;

import com.example.camunda8warehouse.api.model.OrderPost;
import com.example.camunda8warehouse.exceptions.RetryableException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    @SneakyThrows
    public UUID processPayment(OrderPost order) {
        UUID refId = UUID.randomUUID();

        Thread.sleep(Duration.ofSeconds(5).toMillis()); // Validate
        if (order.getQuantity() == 400) { // Some random input to simulate Retryable exception.
            String msg = "Payment service is not responding!";
            log.warn(msg);
            throw new RetryableException(msg);
        }

        log.info("processPayment: Payment processed for order {}, assigned ID {}.", order, refId);
        return refId;
    }
}
