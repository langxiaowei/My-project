package com.example.data.mq;

import com.example.data.dto.DeviceReportMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${iot.mq.report.exchange}") private String exchange;
    @Value("${iot.mq.report.routing-key}") private String routingKey;

    public void send(DeviceReportMsg msg) {
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
    }
}