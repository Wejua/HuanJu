package jieyi.lu.huanjuweflux.RTC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import jakarta.annotation.PostConstruct;

@Component
public class RabbitMQQueueManager {

    @Autowired
    private Sender sender;

    /**
     * 为用户创建个人队列（用于私聊）
     */
    public Mono<Void> createUserQueue(Long userId) {
        String queueName = RabbitMQConfig.PRIVATE_CHAT_QUEUE_PREFIX + userId;
        String routingKey = String.valueOf(userId);

        QueueSpecification queue = QueueSpecification.queue(queueName)
                .durable(true)  // 队列持久化，重启后还在
                .exclusive(false)  // 不排他，多个消费者可以共享
                .autoDelete(false);  // 不自动删除

        BindingSpecification binding = BindingSpecification.binding()
                .exchange(RabbitMQConfig.PRIVATE_CHAT_EXCHANGE)
                .queue(queueName)
                .routingKey(routingKey);

        System.out.println("📦 创建用户队列: " + queueName + " 绑定到 " + RabbitMQConfig.PRIVATE_CHAT_EXCHANGE + " 路由键: " + routingKey);

        return sender.declareQueue(queue)
                .then(sender.bindQueue(binding))
                .doOnSuccess(v -> System.out.println("✅ 用户队列创建成功: " + queueName))
                .doOnError(e -> System.err.println("❌ 创建用户队列失败: " + e.getMessage()))
                .then();
    }

    /**
     * 为用户创建群组队列（用于接收群消息）
     * 每个用户一个队列，绑定到群组路由键
     */
    public Mono<Void> createUserGroupQueue(Long userId, Long groupId) {
        String queueName = RabbitMQConfig.GROUP_CHAT_QUEUE_PREFIX + userId;
        String routingKey = "group." + groupId;

        QueueSpecification queue = QueueSpecification.queue(queueName)
                .durable(true)
                .exclusive(false)
                .autoDelete(false);

        BindingSpecification binding = BindingSpecification.binding()
                .exchange(RabbitMQConfig.GROUP_CHAT_EXCHANGE)
                .queue(queueName)
                .routingKey(routingKey);

        return sender.declareQueue(queue)
                .then(sender.bindQueue(binding))
                .doOnSuccess(v -> System.out.println("✅ 用户群组队列绑定成功: " + queueName + " -> " + routingKey))
                .doOnError(e -> System.err.println("❌ 创建群组队列失败: " + e.getMessage()))
                .then();
    }

    /**
     * 用户加入群组时，绑定群组队列
     */
    public Mono<Void> joinGroup(Long userId, Long groupId) {
        return createUserGroupQueue(userId, groupId);
    }

    /**
     * 用户离开群组时，解绑队列
     */
    public Mono<Void> leaveGroup(Long userId, Long groupId) {
        String queueName = RabbitMQConfig.GROUP_CHAT_QUEUE_PREFIX + userId;
        String routingKey = "group." + groupId;

        BindingSpecification binding = BindingSpecification.binding()
                .exchange(RabbitMQConfig.GROUP_CHAT_EXCHANGE)
                .queue(queueName)
                .routingKey(routingKey);

        return sender.unbindQueue(binding)
                .doOnSuccess(v -> System.out.println("✅ 用户离开群组: " + userId + " 离开群组 " + groupId))
                .then();
    }

    /**
     * 删除用户队列（用户注销时）- 修正版本
     */
    public Mono<Void> deleteUserQueue(Long userId) {
        String queueName = RabbitMQConfig.PRIVATE_CHAT_QUEUE_PREFIX + userId;

        // 修正：deleteQueue 需要更多参数
        // 参数说明：deleteQueue(QueueSpecification, ifUnused, ifEmpty, options)
        return sender.deleteQueue(
                        QueueSpecification.queue(queueName),
                        true,  // ifUnused: 队列未被使用时才删除
                        false, // ifEmpty: 队列不为空时也删除
                        null   // options: 可为null
                )
                .doOnSuccess(v -> System.out.println("✅ 删除用户队列: " + queueName))
                .doOnError(e -> System.err.println("❌ 删除队列失败: " + e.getMessage()))
                .then();
    }

    /**
     * 强制删除用户队列（无论是否使用或为空）
     */
    public Mono<Void> forceDeleteUserQueue(Long userId) {
        String queueName = RabbitMQConfig.PRIVATE_CHAT_QUEUE_PREFIX + userId;

        return sender.deleteQueue(
                        QueueSpecification.queue(queueName),
                        false, // ifUnused: false 表示即使被使用也删除
                        false, // ifEmpty: false 表示即使不为空也删除
                        null
                )
                .doOnSuccess(v -> System.out.println("✅ 强制删除用户队列: " + queueName))
                .doOnError(e -> System.err.println("❌ 强制删除队列失败: " + e.getMessage()))
                .then();
    }

    /**
     * 初始化系统队列（如果需要）
     */
    @PostConstruct
    public void initSystemQueues() {
        System.out.println("🔄 RabbitMQ队列管理器初始化完成");
    }
}