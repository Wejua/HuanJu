package jieyi.lu.huanjuweflux.auth;

import jieyi.lu.huanjuweflux.common.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitListener {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        System.out.println("========== schema.sql和data.sql执行完毕");

        // 更新管理员密码
        userRepository.findByUsername("admin")
                .flatMap(user -> {
                    if ("FLYWAY_PLACEHOLDER".equals(user.getPassword())) {
                        return passwordEncoder.encode("admin123")
                                .flatMap(encodedPassword -> {
                                    user.setPassword(encodedPassword);
                                    return userRepository.save(user);
                                });
                    }
                    return Mono.just(user);
                })
                .doOnSuccess(user -> log.info("管理员密码初始化完成"))
                .doOnError(error -> log.error("初始化密码失败", error))
                .subscribe();
    }
}