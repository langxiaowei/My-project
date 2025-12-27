package com.example.notification.mq;

import com.example.notification.config.AlarmMqConfig;
import com.example.notification.dto.AlarmEventMsg;
import com.example.notification.service.NotificationService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmEventConsumer {
    private final NotificationService notificationService;

    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = AlarmMqConfig.ALARM_Q, ackMode = "MANUAL")
    public void onMessage(AlarmEventMsg msg, Message message, Channel channel) throws IOException {

        long tag = message.getMessageProperties().getDeliveryTag();

        try {
//            // ====== 你先做最简单的：打印日志就是“通知成功” ======
//            log.warn("[NOTIFY] alarm received: deviceId={}, type={}, level={}, content={}",
//                    msg.getDeviceId(), msg.getAlarmType(), msg.getLevel(), msg.getContent());
//            // TODO：后面你想升级：落库 tb_notification / 发邮件 / webhook 都在这里做
//            channel.basicAck(tag, false);
            // 在 @RabbitListener 的方法里
            notificationService.saveNotification(msg);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // ====== 重试最多 3 次，超过就丢 DLQ ======
            Integer retry = (Integer) message.getMessageProperties()
                    .getHeaders().getOrDefault("x-retry", 0);

            if (retry < 3) {
                int nextRetry = retry + 1;
                log.error("[NOTIFY] fail, retry {} -> send to retry queue, msg={}", nextRetry, msg, e);

                // 发到 retry.exchange，进入 retry 队列，10 秒后回到主队列
                rabbitTemplate.convertAndSend(
                        AlarmMqConfig.ALARM_RETRY_EX,
                        AlarmMqConfig.ALARM_RETRY_RK,
                        msg,
                        m -> {
                            m.getMessageProperties().getHeaders().put("x-retry", nextRetry);
                            return m;
                        }
                );

                // 当前这条消费失败：直接 reject，不 requeue（否则会瞬间重复）
                channel.basicReject(tag, false);
            } else {
                log.error("[NOTIFY] fail, retry exceeded -> DLQ, msg={}", msg, e);
                channel.basicReject(tag, false); // 会进入 DLQ（因为主队列配置了 DLX）
            }
        }
    }
}