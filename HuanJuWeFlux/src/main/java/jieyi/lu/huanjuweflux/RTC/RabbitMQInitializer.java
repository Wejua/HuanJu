package jieyi.lu.huanjuweflux.RTC;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.Sender;

@Component
public class RabbitMQInitializer {

    @Autowired
    private Sender sender;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        System.out.println("🔄 应用已启动，开始初始化RabbitMQ交换机...");

        createExchange(RabbitMQConfig.PRIVATE_CHAT_EXCHANGE)
                .then(createExchange(RabbitMQConfig.GROUP_CHAT_EXCHANGE))
                .doOnSuccess(v -> System.out.println("✅ 所有RabbitMQ交换机初始化完成"))
                .doOnError(e -> System.err.println("❌ 交换机初始化失败: " + e.getMessage()))
                .subscribe();
    }

    private Mono<Void> createExchange(String exchangeName) {
        ExchangeSpecification exchange = ExchangeSpecification.exchange(exchangeName)
                .type("topic")
                .durable(true)
                .autoDelete(false);

        return sender.declareExchange(exchange)
                .doOnSuccess(v -> System.out.println("✅ 创建交换机: " + exchangeName))
                .doOnError(e -> {
                    if (e.getMessage() != null && e.getMessage().contains("reply-code=406")) {
                        System.out.println("⚠️ 交换机已存在: " + exchangeName);
                    } else {
                        System.err.println("❌ 创建交换机失败 " + exchangeName + ": " + e.getMessage());
                    }
                })
                .onErrorResume(e -> {
                    // 如果交换机已存在（406错误），忽略错误继续执行
                    if (e.getMessage() != null && e.getMessage().contains("reply-code=406")) {
                        return Mono.empty();
                    }
                    return Mono.error(e);
                })
                .then(); // 关键：添加 then() 将 Mono<DeclareOk> 转换为 Mono<Void>
    }
}