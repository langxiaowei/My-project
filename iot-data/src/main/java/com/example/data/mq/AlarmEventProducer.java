package com.example.data.mq;

import com.example.data.config.AlarmMqConfig;
import com.example.data.dto.AlarmEventMsg;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(AlarmEventMsg msg) {
        rabbitTemplate.convertAndSend(AlarmMqConfig.ALARM_EX, AlarmMqConfig.ALARM_RK, msg);
    }
}