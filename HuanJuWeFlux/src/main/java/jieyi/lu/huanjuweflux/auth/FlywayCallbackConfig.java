package jieyi.lu.huanjuweflux.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FlywayCallbackConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("dev")  // 仅在开发环境执行
    public Callback afterMigrateCallback() {
        return new Callback() {
            @Override
            public boolean supports(Event event, Context context) {
                return event == Event.AFTER_MIGRATE;
            }

            @Override
            public boolean canHandleInTransaction(Event event, Context context) {
                return false;
            }

            @Override
            public void handle(Event event, Context context) {
                log.info("Flyway 迁移完成，开始初始化用户密码...");

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

            @Override
            public String getCallbackName() {
                return "Admin Password Initializer";
            }
        };
    }
}