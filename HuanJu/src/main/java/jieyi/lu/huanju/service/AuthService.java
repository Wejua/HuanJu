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

    /*
        客户端请求 /api/login
        ↓
    1. JwtAuthenticationFilter.doFilterInternal()  ← ⭐ 过滤器先执行！
        ↓
    2. DispatcherServlet 处理请求
        ↓
    3. 找到对应的 Controller 方法
        ↓
    4. AuthController.login()  ← ⭐ Controller 方法后执行！
        ↓
    5. 返回响应
    * */
    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        // 1. 验证用户是否存在
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误")); // Lambda表达式：() -> new RuntimeException() 匹配任何无参、返回RuntimeException的接口方法, 这里就匹配了get()方法

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
