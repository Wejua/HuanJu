package jieyi.lu.huanjuweflux.auth;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DisableSecurityConfig {
    @Autowired
    private Environment environment;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        log.info("========== Flyway 配置检查 ==========");

        // 检查 Flyway 是否启用
        String flywayEnabled = environment.getProperty("spring.flyway.enabled");
        log.info("spring.flyway.enabled = {}", flywayEnabled);

        // 检查脚本位置
        String locations = environment.getProperty("spring.flyway.locations");
        log.info("spring.flyway.locations = {}", locations);

        // 检查数据源
        String url = environment.getProperty("spring.datasource.url");
        log.info("spring.datasource.url = {}", url);

        log.info("=====================================");
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .build();
    }
}

