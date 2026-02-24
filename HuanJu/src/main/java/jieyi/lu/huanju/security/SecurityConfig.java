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

    // 这个方法的作用：在应用启动时，把安全规则（放行哪些接口、添加什么过滤器）配置好，构建出一个 SecurityFilterChain 对象，然后把这个对象放进 Spring 容器。
    // 方法名可以随意改，但是参数和返回值类型不能改
    // FilterChainProxy 会遍历所有 SecurityFilterChain， 所以可以有多个 SecurityFilterChain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable) // 关闭 CSRF 防护, CSRF 是为了防止表单重复提交攻击,在前后端分离 + JWT + 无状态应用中，不需要
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 设置会话管理策略为：无状态, REST API 应该是无状态的, 每次请求都带 Token，服务器不保存客户端状态
                .authorizeHttpRequests(auth -> auth // 请求授权配置
                        .requestMatchers("/api/login", "/api/login/test", "/api/register").permitAll() // 配置工公开接口  requestMatchers() - 匹配特定URL  permitAll() - 允许所有人访问（无需登录）
                        .anyRequest().authenticated() // 其他都要登录. anyRequest() - 所有其他请求  authenticated() - 需要认证
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService()), // 添加自定义过滤器
                        UsernamePasswordAuthenticationFilter.class); // 在这个过滤器之前执行
                /* 过滤器有很多，上面这行代码是放在表单登录过滤器前
                1. WebAsyncManagerIntegrationFilter      ← 处理异步请求
                2. SecurityContextPersistenceFilter     ← 恢复/保存 SecurityContext
                3. HeaderWriterFilter                   ← 添加安全响应头
                4. CorsFilter                           ← 处理跨域
                5. CsrfFilter                           ← CSRF防护
                6. LogoutFilter                         ← 处理登出
                7. JwtAuthenticationFilter               ← ⭐ 你的过滤器！
                8. UsernamePasswordAuthenticationFilter  ← 表单登录
                9. DefaultLoginPageGeneratingFilter     ← 生成默认登录页
                10. DefaultLogoutPageGeneratingFilter    ← 生成默认登出页
                11. BasicAuthenticationFilter            ← HTTP Basic认证
                12. RequestCacheAwareFilter              ← 请求缓存
                13. SecurityContextHolderAwareRequestFilter ← 包装请求
                14. AnonymousAuthenticationFilter        ← 匿名用户
                15. SessionManagementFilter              ← 会话管理
                16. ExceptionTranslationFilter           ← 异常转换
                17. FilterSecurityInterceptor            ← 最后权限检查
                * */

        return http.build(); // 把上面所有的配置，构建成一个真正的 SecurityFilterChain 对象
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // ⭐ 第1层：这个方法本身 - 只在启动时执行一次！
        System.out.println("1. userDetailsService() 方法执行了（启动时）");

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
            // ⭐ 第2层：这个 Lambda 体 - 每次认证时执行！
            System.out.println("2. loadUserByUsername() 执行了，用户名: " + username);
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

