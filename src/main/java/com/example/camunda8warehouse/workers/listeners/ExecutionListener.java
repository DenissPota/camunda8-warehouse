package com.example.camunda8warehouse.workers.listeners;

import com.example.camunda8warehouse.utils.TimeUtil;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ExecutionListener {

    @JobWorker(type = "log-start-time")
    public Map<String, Long> logStartTime() {
        return Map.of("startTime", System.currentTimeMillis());
    }

    @JobWorker(type = "log-end-time")
    public void logEndTime(ActivatedJob activatedJob, @Variable("startTime") long startTime) {
        log.info("Processed {} in {}.", activatedJob.getKey(), TimeUtil.getDuration(startTime));
    }
}
