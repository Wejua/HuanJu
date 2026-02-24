package jieyi.lu.huanju.service;

import jieyi.lu.huanju.dto.LoginRequest;
import jieyi.lu.huanju.dto.LoginResponse;
import jieyi.lu.huanju.dto.RegisterRequest;
import jieyi.lu.huanju.dto.RegisterResponse;
import jieyi.lu.huanju.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jieyi.lu.huanju.repository.UserRepository;
import jieyi.lu.huanju.security.JwtUtil;

@Slf4j
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
    
    @Transactional // 自动开启、提交或回滚数据库事务，保证数据操作要么全部成功，要么回到原点
    public RegisterResponse register(RegisterRequest request) {
        log.info("开始注册用户: {}", request.getUsername());

        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("注册失败：用户名已存在 - {}", request.getUsername());
            throw new RuntimeException("用户名已存在");
        }
        // 2. 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("注册失败：邮箱已被注册 - {}", request.getEmail());
            throw new RuntimeException("邮箱已被注册");
        }
        // 3. 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 加密密码
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setRole("USER"); // 默认角色
        user.setEnabled(true);
        // 4. 保存到数据库
        User savedUser = userRepository.save(user);
        log.info("用户注册成功: {}, id: {}", savedUser.getUsername(), savedUser.getId());
        // 5. 构建响应
        return RegisterResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .nickname(savedUser.getNickname())
                .role(savedUser.getRole())
                .createdAt(savedUser.getCreatedAt())
                .message("注册成功")
                .build();
    }
}
