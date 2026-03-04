package jieyi.lu.huanjuweflux.RTC;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class IMWebSocketHandler implements WebSocketHandler {

    // 存储所有在线用户的WebSocket会话
    private static final Map<Long, WebSocketSession> ONLINE_USERS = new ConcurrentHashMap<>();
    private static final Map<Long, Sinks.Many<MessageDTO>> USER_SINKS = new ConcurrentHashMap<>();

    @Autowired
    private IMService imService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageEncryptor messageEncryptor;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 从请求参数中获取用户ID (实际应该从JWT中解析)
        String userIdStr = session.getHandshakeInfo().getUri().getQuery();
        Long userId = extractUserId(userIdStr);

        if (userId == null) {
            return session.close();
        }

        // 存储用户会话
        ONLINE_USERS.put(userId, session);

        // 创建消息处理器
        Sinks.Many<MessageDTO> sink = Sinks.many().multicast().onBackpressureBuffer();
        USER_SINKS.put(userId, sink);

        // 为用户创建RabbitMQ队列
        imService.createUserQueues(userId)
                .doOnSuccess(v -> log.info("为用户 {} 创建RabbitMQ队列成功", userId))
                .doOnError(e -> log.error("为用户 {} 创建RabbitMQ队列失败: {}", userId, e.getMessage()))
                .subscribe();

        // 处理接收的消息
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(payload -> handleIncomingMessage(userId, payload))
                .doOnError(e -> log.error("Error handling message: {}", e.getMessage()))
                .then();

        // 发送消息给客户端（加入加密）
        Mono<Void> output = sink.asFlux()
                .flatMap(message -> sendMessage(session, message))
                .doOnError(e -> log.error("Error sending message: {}", e.getMessage()))
                .then();

        // 组合输入输出
        return Mono.zip(input, output)
                .doFinally(signalType -> {
                    // 清理资源
                    ONLINE_USERS.remove(userId);
                    USER_SINKS.remove(userId);
                    log.info("User {} disconnected", userId);
                })
                .then();
    }

    private Mono<Void> handleIncomingMessage(Long fromUserId, String payload) {
        try {
            MessageDTO message = objectMapper.readValue(payload, MessageDTO.class);
            message.setFromUserId(fromUserId);
            message.setTimestamp(java.time.LocalDateTime.now());

            // 解密收到的消息（如果是加密的）
            if (message.getContent() != null && !message.getContent().isEmpty()) {
                try {
                    String decryptedContent = messageEncryptor.decrypt(message.getContent());
                    message.setContent(decryptedContent);
                } catch (Exception e) {
                    log.warn("消息解密失败，使用原内容: {}", e.getMessage());
                    // 解密失败，继续使用原内容（可能是明文）
                }
            }

            switch (message.getAction()) {
                case 1: // 发送消息
                    return imService.processMessage(message)
                            .doOnSuccess(result -> {
                                if (result != null && result.getCode() == 200) {
                                    // 转发消息给接收者
                                    forwardMessage(result);
                                }
                            })
                            .then();
                case 2: // 心跳
                    return Mono.just(message)
                            .map(m -> {
                                m.setCode(200);
                                m.setMessage("pong");
                                return m;
                            })
                            .flatMap(m -> sendMessageToUser(fromUserId, m));
                default:
                    return Mono.empty();
            }
        } catch (IOException e) {
            log.error("Failed to parse message: {}", e.getMessage());
            return Mono.empty();
        }
    }

    private void forwardMessage(MessageDTO message) {
        if (message.getType() == 1) { // 私聊
            sendMessageToUser(message.getToId(), message).subscribe();
        } else if (message.getType() == 2) { // 群聊
            // 群聊消息转发逻辑
            imService.getGroupMembers(message.getToId())
                    .flatMap(member -> sendMessageToUser(member.getUserId(), message))
                    .subscribe();
        }
    }

    private Mono<Void> sendMessageToUser(Long userId, MessageDTO message) {
        Sinks.Many<MessageDTO> sink = USER_SINKS.get(userId);
        if (sink != null) {
            sink.tryEmitNext(message);
            return Mono.empty();
        }
        return Mono.empty();
    }

    /**
     * 发送消息（加密版本）
     */
    private Mono<Void> sendMessage(WebSocketSession session, MessageDTO message) {
        try {
            // 对消息内容加密
            if (message.getContent() != null && !message.getContent().isEmpty()) {
                String encryptedContent = messageEncryptor.encrypt(message.getContent());
                message.setContent(encryptedContent);
            }

            String payload = objectMapper.writeValueAsString(message);
            return session.send(Mono.just(session.textMessage(payload)));
        } catch (Exception e) {
            log.error("Failed to send message: {}", e.getMessage());
            return Mono.empty();
        }
    }

    private Long extractUserId(String query) {
        if (query != null && query.startsWith("userId=")) {
            try {
                return Long.parseLong(query.substring(7));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}