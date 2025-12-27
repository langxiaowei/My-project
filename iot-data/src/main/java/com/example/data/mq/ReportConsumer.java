package com.example.data.mq;

import com.example.data.dto.DeviceReportMsg;
import com.example.data.service.ReportProcessService;
import com.example.data.service.impl.ReportDedupRedis;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportConsumer {

    private final ReportProcessService reportProcessService;
    private final ReportDedupRedis reportDedupRedis;

    @RabbitListener(queues = "${iot.mq.report.queue}")
    public void onMessage(DeviceReportMsg msg, Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            // 消费侧幂等（强烈建议）
            if (!reportDedupRedis.allow(msg.getMsgId())) {
                log.warn("[report-mq] duplicate msgId={}, ack", msg.getMsgId());
                channel.basicAck(tag, false);
                return;
            }

            reportProcessService.process(msg);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // 不重回队列，走死信（你 yml 里 default-requeue-rejected=false）
            log.error("[report-mq] fail msgId={}, deviceId={}", msg.getMsgId(), msg.getDeviceId(), e);
            channel.basicReject(tag, false);
        }
    }
}