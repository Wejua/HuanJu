package jieyi.lu.huanjuweflux.RTC;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;
import org.springframework.context.annotation.Lazy;

@Service
public class RabbitMQService {

    @Autowired
    @Lazy
    private Sender sender;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 发送消息到 RabbitMQ
     */
    public Mono<Void> sendMessage(String exchange, String routingKey, Object message) {
        return Mono.fromCallable(() -> {
                    byte[] body = objectMapper.writeValueAsBytes(message);
                    return new OutboundMessage(exchange, routingKey, body);
                }).subscribeOn(Schedulers.boundedElastic())
                .flatMap(outboundMessage ->
                        sender.send(Mono.just(outboundMessage))
                )
                .onErrorResume(e -> {
                    System.err.println("发送消息到RabbitMQ失败: " + e.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * 发送私聊消息
     */
    public Mono<Void> sendPrivateMessage(Long toUserId, Object message) {
        String routingKey = String.valueOf(toUserId);
        return sendMessage(RabbitMQConfig.PRIVATE_CHAT_EXCHANGE, routingKey, message);
    }

    /**
     * 发送群聊消息
     */
    public Mono<Void> sendGroupMessage(Long groupId, Object message) {
        String routingKey = "group." + groupId;
        return sendMessage(RabbitMQConfig.GROUP_CHAT_EXCHANGE, routingKey, message);
    }
}
