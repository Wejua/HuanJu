package jieyi.lu.huanju.security;

import jieyi.lu.huanju.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jieyi.lu.huanju.repository.UserRepository;

@Component // 这个类只有一个构造方法，Spring Boot默认用这个生成 @Bean 单例。 如果有多个构造方法，要用 @Autowired 来指定用哪个
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository; // final 字段，线程安全, 不可变
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 创建测试用户（仅用于测试）
        if (!userRepository.existsByUsername("admin")) {
            User user = new User();
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("admin123"));
            user.setEmail("admin@example.com");
            user.setRole("ADMIN");
            user.setEnabled(true);
            userRepository.save(user);
            System.out.println("测试用户已创建 - 用户名: admin, 密码: admin123");
        }
    }
}
