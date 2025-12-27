package com.example.data.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Value("${iot.mq.report.exchange}") private String exchange;
    @Value("${iot.mq.report.routing-key}") private String routingKey;
    @Value("${iot.mq.report.queue}") private String queue;

    @Value("${iot.mq.report.dlx-exchange}") private String dlxExchange;
    @Value("${iot.mq.report.dlq}") private String dlq;

    @Bean
    public DirectExchange reportExchange() {
        return new DirectExchange(exchange, true, false);
    }

    @Bean
    public DirectExchange reportDlxExchange() {
        return new DirectExchange(dlxExchange, true, false);
    }

    @Bean
    public Queue reportQueue() {
        // 失败/拒绝 -> 进死信交换机 -> 死信队列
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", dlxExchange);
        args.put("x-dead-letter-routing-key", routingKey);
        return new Queue(queue, true, false, false, args);
    }

    @Bean
    public Queue reportDlq() {
        return new Queue(dlq, true);
    }

    @Bean
    public Binding reportBinding() {
        return BindingBuilder.bind(reportQueue()).to(reportExchange()).with(routingKey);
    }

    @Bean
    public Binding reportDlqBinding() {
        return BindingBuilder.bind(reportDlq()).to(reportDlxExchange()).with(routingKey);
    }
}
