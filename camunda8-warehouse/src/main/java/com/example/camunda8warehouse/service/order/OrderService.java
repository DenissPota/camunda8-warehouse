package com.example.camunda8warehouse.service.order;

import com.example.camunda8warehouse.api.model.OrderPost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    public UUID processOrder(OrderPost order) {
        log.info("Processing order: {} with quantity {}.", order.getName(), order.getQuantity());
        return UUID.randomUUID();
    }
}
