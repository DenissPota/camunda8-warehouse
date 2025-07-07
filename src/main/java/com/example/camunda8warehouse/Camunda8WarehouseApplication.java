package com.example.camunda8warehouse;

import com.example.camunda8warehouse.api.model.OrderPost;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;

@Slf4j
@Deployment(resources = "classpath:process-orders.bpmn")
@RequiredArgsConstructor
@SpringBootApplication
public class Camunda8WarehouseApplication implements CommandLineRunner {

    private final ZeebeClient zeebeClient;

    public static void main(String[] args) {
        SpringApplication.run(Camunda8WarehouseApplication.class, args);
    }

    @Override
    public void run(final String... args) {
        var bpmnProcessId = "process-orders";
        var event = zeebeClient.newCreateInstanceCommand()
            .bpmnProcessId(bpmnProcessId)
            .latestVersion()
            .variables(Map.of("order", OrderPost.builder().name("Nvidia RTX 3090").quantity(7).build()))
            .send()
            .join();
        log.info("Started a process instance: {}", event.getProcessInstanceKey());
    }
}
