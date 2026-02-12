package jieyi.lu.huanju.security;

import jieyi.lu.huanju.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jieyi.lu.huanju.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
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
