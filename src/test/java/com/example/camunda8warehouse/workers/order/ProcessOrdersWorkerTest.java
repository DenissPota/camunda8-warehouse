package com.example.camunda8warehouse.workers.order;

import com.example.camunda8warehouse.api.model.OrderPost;
import com.example.camunda8warehouse.services.email.EmailService;
import com.example.camunda8warehouse.services.payment.PaymentService;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Map;
import java.util.UUID;

import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.waitForProcessInstanceCompleted;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@SpringBootTest
@ZeebeSpringTest
class ProcessOrdersWorkerTest {

    private static final UUID MOCK_UUID = UUID.fromString("95b9787d-8b92-4245-ac13-42586f42f301");

    @MockitoBean
    private PaymentService paymentService;
    @MockitoBean
    private EmailService emailService;

    @Autowired
    private ZeebeTestEngine engine;
    @Autowired
    private ZeebeClient client;

    @BeforeEach
    public void setup() {
        DeploymentEvent deploymentEvent = client.newDeployResourceCommand()
            .addResourceFromClasspath("process-orders.bpmn")
            .send()
            .join();
    }

    @Test
    public void processOrders_paymentSuccess_sendsInvoicesAndPacksItems() {
        //given
        var order = Map.of("order", OrderPost.builder().name("Nvidia RTX 3090").quantity(100).build());
        given(paymentService.processPayment(order.get("order"))).willReturn(MOCK_UUID);

        // when then
        ProcessInstanceEvent processInstance = startInstance("process-orders", order);

        waitForProcessInstanceCompleted(processInstance);
        BpmnAssert.assertThat(processInstance)
            .hasPassedElement("process-payment")
            .hasVariableWithValue("orderReferenceId", MOCK_UUID)
            .hasPassedElement("send-invoice")
            .hasPassedElement("pack-items")
            .hasNotPassedElement("send-email")
            .isCompleted();
    }

    @Test
    public void processOrders_orderQuantityTooLard_validationException() {
        //given
        var order = Map.of("order", OrderPost.builder().name("Nvidia RTX 3090").quantity(1001).build());

        // when then
        ProcessInstanceEvent processInstance = startInstance("process-orders", order);

        waitForProcessInstanceCompleted(processInstance);
        BpmnAssert.assertThat(processInstance)
            .hasNotPassedElement("process-payment")
            .hasNotPassedElement("send-invoice")
            .hasNotPassedElement("pack-items")
            .hasPassedElement("validation-exception")
            .hasPassedElement("inform-customer")
            .isCompleted();

        then(emailService).should().sendEmail(any(), any(), any());
    }

    private ProcessInstanceEvent startInstance(String id, Map<String, OrderPost> variables) {
        return client.newCreateInstanceCommand()
            .bpmnProcessId(id)
            .latestVersion()
            .variables(variables)
            .send().join();
    }
}
