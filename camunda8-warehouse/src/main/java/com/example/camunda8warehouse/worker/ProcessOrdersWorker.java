package com.example.camunda8warehouse.worker;

import com.example.camunda8warehouse.api.model.OrderPost;
import com.example.camunda8warehouse.service.order.OrderService;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessOrdersWorker {

    private final OrderService orderService;

    @JobWorker(type = "process-order")
    public Map<String, UUID> processOrders(@Variable(name = "order") OrderPost order) {
        log.info("Incoming order: {}.", order);
        UUID orderReferenceNumber = orderService.processOrder(order);
        return Map.of("orderReferenceNumber", orderReferenceNumber);
    }

    @JobWorker(type = "send-invoice")
    public void sendInvoice(@Variable(name = "orderReferenceNumber") UUID orderReferenceNumber) {
        log.info("Sending invoice for order {}.", orderReferenceNumber);
    }
}
