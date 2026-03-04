package jieyi.lu.huanjuweflux.RTC;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

@Configuration
public class RabbitMQConfig {

    public static final String PRIVATE_CHAT_EXCHANGE = "im.private.exchange";
    public static final String GROUP_CHAT_EXCHANGE = "im.group.exchange";
    public static final String PRIVATE_CHAT_QUEUE_PREFIX = "im.private.queue.";
    public static final String GROUP_CHAT_QUEUE_PREFIX = "im.group.queue.";

    @Value("${spring.rabbitmq.host:localhost}")
    private String host;

    @Value("${spring.rabbitmq.port:5672}")
    private int port;

    @Value("${spring.rabbitmq.username:guest}")
    private String username;

    @Value("${spring.rabbitmq.password:guest}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;

    @Bean
    public ConnectionFactory rabbitConnectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);

        connectionFactory.setAutomaticRecoveryEnabled(true);
        connectionFactory.setNetworkRecoveryInterval(5000);

        return connectionFactory;
    }

    @Bean
    public SenderOptions senderOptions(ConnectionFactory rabbitConnectionFactory) {
        return new SenderOptions()
                .connectionFactory(rabbitConnectionFactory)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public ReceiverOptions receiverOptions(ConnectionFactory rabbitConnectionFactory) {
        return new ReceiverOptions()
                .connectionFactory(rabbitConnectionFactory);
    }

    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }

    @Bean
    public Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }
}