package com.example.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlarmMqConfig {

    // ====== 名字你也可以放到 yml，用 @Value 注入，这里先写死，最稳 ======
    public static final String ALARM_EX = "alarm.ex";
    public static final String ALARM_Q = "alarm.q";
    public static final String ALARM_RK = "alarm";

    public static final String ALARM_DLX = "alarm.dlx";
    public static final String ALARM_DLQ = "alarm.dlq";
    public static final String ALARM_DLK = "alarm.dlq";

    public static final String ALARM_RETRY_EX = "alarm.retry.ex";
    public static final String ALARM_RETRY_Q = "alarm.retry.q";
    public static final String ALARM_RETRY_RK = "alarm.retry";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // 解决你之前遇到的：SimpleMessageConverter 不能发对象 -> 必须用 JSON Converter
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange alarmExchange() {
        return new DirectExchange(ALARM_EX, true, false);
    }

    @Bean
    public DirectExchange alarmDlx() {
        return new DirectExchange(ALARM_DLX, true, false);
    }

    @Bean
    public DirectExchange alarmRetryExchange() {
        return new DirectExchange(ALARM_RETRY_EX, true, false);
    }

    @Bean
    public Queue alarmQueue() {
        return QueueBuilder.durable(ALARM_Q)
                .withArgument("x-dead-letter-exchange", ALARM_DLX)
                .withArgument("x-dead-letter-routing-key", ALARM_DLK)
                .build();
    }

    @Bean
    public Queue alarmDlq() {
        return QueueBuilder.durable(ALARM_DLQ).build();
    }

    @Bean
    public Queue alarmRetryQueue() {
        // 重试队列：10秒后自动死信到主交换机（回到 alarm.q）
        return QueueBuilder.durable(ALARM_RETRY_Q)
                .withArgument("x-message-ttl", 10_000)
                .withArgument("x-dead-letter-exchange", ALARM_EX)
                .withArgument("x-dead-letter-routing-key", ALARM_RK)
                .build();
    }

    @Bean
    public Binding alarmBinding() {
        return BindingBuilder.bind(alarmQueue()).to(alarmExchange()).with(ALARM_RK);
    }

    @Bean
    public Binding alarmDlqBinding() {
        return BindingBuilder.bind(alarmDlq()).to(alarmDlx()).with(ALARM_DLK);
    }

    @Bean
    public Binding alarmRetryBinding() {
        return BindingBuilder.bind(alarmRetryQueue()).to(alarmRetryExchange()).with(ALARM_RETRY_RK);
    }
}