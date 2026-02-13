package jieyi.lu.huanju.security;

import jieyi.lu.huanju.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import jieyi.lu.huanju.repository.UserRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // 开启 Web 安全功能
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable) // 关闭 CSRF 防护, CSRF 是为了防止表单重复提交攻击,在前后端分离 + JWT + 无状态应用中，不需要
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 设置会话管理策略为：无状态, REST API 应该是无状态的, 每次请求都带 Token，服务器不保存客户端状态
                .authorizeHttpRequests(auth -> auth // 请求授权配置
                        .requestMatchers("/api/login", "/api/login/test").permitAll() // 配置工公开接口  requestMatchers() - 匹配特定URL  permitAll() - 允许所有人访问（无需登录）
                        .anyRequest().authenticated() // 其他都要登录. anyRequest() - 所有其他请求  authenticated() - 需要认证
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService()), // 添加自定义过滤器
                        UsernamePasswordAuthenticationFilter.class); // 在这个过滤器之前执行

        return http.build(); // 把上面所有的配置，构建成一个真正的 SecurityFilterChain 对象
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // 返回一个 Lambda 表达式  等价于：
        /*
            return new UserDetailsService() {
                @Override
                public UserDetails loadUserByUsername(String username) {
                    // 根据用户名查询用户
                    return userDetails;
                }
            };
         */
        return username -> {
            // 1. 从数据库查用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
            // 2. 把 User 实体转换成 Spring Security 的 UserDetails 对象
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername()) // 设置用户名
                    .password(user.getPassword()) // 设置密码（已加密）
                    .roles(user.getRole()) // 设置角色
                    .disabled(!user.getEnabled()) // 是否禁用
                    .build(); // 构建对象
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

