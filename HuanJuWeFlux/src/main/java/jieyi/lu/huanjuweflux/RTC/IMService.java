package jieyi.lu.huanjuweflux.RTC;

import jieyi.lu.huanjuweflux.common.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class IMService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RabbitMQService rabbitMQService;

    @Autowired
    private RabbitMQQueueManager rabbitMQQueueManager;

    private static final String USER_STATUS_KEY = "user:status:";
    private static final String LATEST_MESSAGE_KEY = "latest:message:";

    /**
     * 处理消息
     */
    public Mono<MessageDTO> processMessage(MessageDTO messageDTO) {
        String msgId = UUID.randomUUID().toString();
        messageDTO.setMsgId(msgId);
        messageDTO.setCode(200);

        // 1. 保存到Redis热数据
        return saveMessageToRedis(messageDTO)
                // 2. 保存到MongoDB持久化
                .flatMap(dto -> saveMessageToMongo(dto))
                // 3. 发送到消息队列
                .flatMap(dto -> sendMessageToMQ(dto))
                // 4. 更新最新消息缓存
                .flatMap(dto -> updateLatestMessageCache(dto))
                // 5. 返回处理结果
                .defaultIfEmpty(messageDTO);
    }

    /**
     * 保存消息到Redis (热数据)
     */
    private Mono<MessageDTO> saveMessageToRedis(MessageDTO dto) {
        String key = dto.getType() == 1
                ? LATEST_MESSAGE_KEY + "private:" + dto.getFromUserId() + ":" + dto.getToId()
                : LATEST_MESSAGE_KEY + "group:" + dto.getToId();

        return redisTemplate.opsForList()
                .leftPush(key, dto)
                .flatMap(len -> {
                    if (len > 100) { // 只保留最近100条
                        return redisTemplate.opsForList().trim(key, 0, 99);
                    }
                    return Mono.just(true);
                })
                .thenReturn(dto);
    }

    /**
     * 保存消息到MongoDB (持久化)
     */
    private Mono<MessageDTO> saveMessageToMongo(MessageDTO dto) {
        Message message = new Message();
        message.setMsgId(dto.getMsgId());
        message.setType(dto.getType());
        message.setFromUserId(dto.getFromUserId());
        message.setToId(dto.getToId());
        message.setContent(dto.getContent());
        message.setContentType(dto.getContentType());
        message.setStatus(1); // 已发送
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message)
                .map(saved -> dto)
                .onErrorResume(e -> {
                    log.error("Failed to save message to MongoDB: {}", e.getMessage());
                    return Mono.just(dto);
                });
    }

    /**
     * 发送消息到RabbitMQ
     */
    private Mono<MessageDTO> sendMessageToMQ(MessageDTO dto) {
        String exchange = dto.getType() == 1
                ? RabbitMQConfig.PRIVATE_CHAT_EXCHANGE
                : RabbitMQConfig.GROUP_CHAT_EXCHANGE;

        String routingKey = dto.getType() == 1
                ? String.valueOf(dto.getToId())
                : "group." + dto.getToId();

        return rabbitMQService.sendMessage(exchange, routingKey, dto)
                .thenReturn(dto)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(e -> {
                    log.error("Failed to send message to RabbitMQ: {}", e.getMessage());
                    return Mono.just(dto);
                });
    }

    /**
     * 更新最新消息缓存
     */
    private Mono<MessageDTO> updateLatestMessageCache(MessageDTO dto) {
        String key = USER_STATUS_KEY + dto.getFromUserId();
        return redisTemplate.opsForValue()
                .set(key, LocalDateTime.now().toString(), Duration.ofHours(24))
                .thenReturn(dto);
    }

    /**
     * 更新用户在线状态
     */
    public Mono<Boolean> updateUserStatus(Long userId, boolean online) {
        String key = USER_STATUS_KEY + userId;
        if (online) {
            return redisTemplate.opsForValue()
                    .set(key, "online", Duration.ofMinutes(30));
        } else {
            return redisTemplate.opsForValue()
                    .delete(key)
                    .map(deleted -> true);
        }
    }

    /**
     * 获取用户在线状态
     */
    public Mono<Boolean> isUserOnline(Long userId) {
        String key = USER_STATUS_KEY + userId;
        return redisTemplate.hasKey(key);
    }

    /**
     * 获取群组成员 (简化版)
     */
    public Flux<GroupMember> getGroupMembers(Long groupId) {
        // 实际应该从数据库查询
        return Flux.just(
                new GroupMember(1L, groupId, 1L),
                new GroupMember(2L, groupId, 2L)
        );
    }

    /**
     * 获取历史消息
     */
    public Flux<MessageDTO> getHistoryMessages(Long userId, Long targetId, Integer type, int page, int size) {
        if (type == 1) { // 私聊
            return messageRepository.findByTypeAndFromUserIdAndToIdOrderByCreatedAtDesc(
                            type, userId, targetId, org.springframework.data.domain.PageRequest.of(page, size))
                    .map(this::convertToDTO);
        } else { // 群聊
            return messageRepository.findByTypeAndToIdOrderByCreatedAtDesc(
                            type, targetId, org.springframework.data.domain.PageRequest.of(page, size))
                    .map(this::convertToDTO);
        }
    }

    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMsgId(message.getMsgId());
        dto.setType(message.getType());
        dto.setFromUserId(message.getFromUserId());
        dto.setToId(message.getToId());
        dto.setContent(message.getContent());
        dto.setContentType(message.getContentType());
        dto.setTimestamp(message.getCreatedAt());
        dto.setCode(200);
        return dto;
    }

    /**
     * 为用户创建所有需要的队列
     */
    public Mono<Void> createUserQueues(Long userId) {
        return rabbitMQQueueManager.createUserQueue(userId)
                .then();
    }

    /**
     * 用户加入群组
     */
    public Mono<Void> joinGroup(Long userId, Long groupId) {
        return rabbitMQQueueManager.joinGroup(userId, groupId);
    }

    /**
     * 用户离开群组
     */
    public Mono<Void> leaveGroup(Long userId, Long groupId) {
        return rabbitMQQueueManager.leaveGroup(userId, groupId);
    }

    // 内部类
    public static class GroupMember {
        private Long id;
        private Long groupId;
        private Long userId;

        public GroupMember(Long id, Long groupId, Long userId) {
            this.id = id;
            this.groupId = groupId;
            this.userId = userId;
        }

        public Long getId() { return id; }
        public Long getGroupId() { return groupId; }
        public Long getUserId() { return userId; }
    }
}