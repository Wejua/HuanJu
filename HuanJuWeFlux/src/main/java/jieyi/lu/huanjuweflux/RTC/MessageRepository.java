package jieyi.lu.huanjuweflux.RTC;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    // 查询私聊消息
    Flux<Message> findByTypeAndFromUserIdAndToIdOrderByCreatedAtDesc(
            Integer type, Long fromUserId, Long toId, Pageable pageable);

    // 查询群聊消息
    Flux<Message> findByTypeAndToIdOrderByCreatedAtDesc(
            Integer type, Long toId, Pageable pageable);

    // 更新消息状态
    Mono<Message> findByMsgId(String msgId);
}