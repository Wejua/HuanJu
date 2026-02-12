package jieyi.lu.huanju.service;

import jieyi.lu.huanju.dto.LoginRequest;
import jieyi.lu.huanju.dto.LoginResponse;
import jieyi.lu.huanju.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jieyi.lu.huanju.repository.UserRepository;
import jieyi.lu.huanju.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 验证用户是否存在
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 2. 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 验证用户是否启用
        if (!user.getEnabled()) {
            throw new RuntimeException("账户已被禁用");
        }

        // 4. 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername());

        // 5. 返回登录响应
        return new LoginResponse(token, user.getId(), user.getUsername(),
                user.getEmail(), user.getRole());
    }
}
