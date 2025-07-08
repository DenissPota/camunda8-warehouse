package com.example.camunda8warehouse.workers.order;

import com.example.camunda8warehouse.api.model.OrderPost;
import com.example.camunda8warehouse.exceptions.RetryableException;
import com.example.camunda8warehouse.services.payment.PaymentService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class ProcessOrdersWorker {

    private static final String INVALID_INPUT_ERROR_CODE = "invalid-input-error";
    private static final String GLOBAL_ERROR_CODE = "global-error";

    private final PaymentService paymentService;

    @JobWorker(type = "process-payment", timeout = 30000, maxJobsActive = 5, autoComplete = false)
    public void processPayment(@Variable OrderPost order, ActivatedJob job, JobClient client) {
        try {
            log.debug("Activated job: {}", job);
            log.info("Incoming order: {} with quantity {}.", order.getName(), order.getQuantity());
            if (order.getQuantity() > 1000) {
                throw new IllegalArgumentException("Order quantity is too high!");
            }
            UUID refId = paymentService.processPayment(order);

            // Manually complete the job with variables
            client.newCompleteCommand(job.getKey())
                .variables(Map.of("orderReferenceId", refId))
                .send()
                .join();
        } catch (RetryableException reEx) {
            client.newFailCommand(job.getKey())
                .retries(job.getRetries() - 1)
                .retryBackoff(Duration.ofSeconds(5))
                .errorMessage(reEx.getMessage())
                .send()
                .join();
        } catch (IllegalArgumentException iEx) {
            client.newThrowErrorCommand(job.getKey())
                .errorCode(INVALID_INPUT_ERROR_CODE)
                .send()
                .join();
        } catch (Exception e) {
            client.newThrowErrorCommand(job.getKey())
                .errorCode(GLOBAL_ERROR_CODE)
                .send()
                .join();
        }
    }

    @JobWorker(type = "send-invoice")
    public void sendInvoice(@Variable(name = "orderReferenceId") UUID refId) {
        log.info("Sending invoice for order {}.", refId);
    }

    @JobWorker(type = "pack-items")
    public void packItems(@Variable(name = "orderReferenceId") UUID refId) {
        log.info("Packing items for order {}.", refId);
    }

}
