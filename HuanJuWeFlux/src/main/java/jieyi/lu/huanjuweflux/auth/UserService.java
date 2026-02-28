package jieyi.lu.huanjuweflux.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @Transactional
    public Mono<UserDTO.UserInfoResponse> register(UserDTO.RegisterRequest request, String ip) {
        return validateRegisterRequest(request)
                .flatMap(valid -> {
                    User user = User.builder()
                            .username(request.getUsername())
                            .email(request.getEmail())
                            .phone(request.getPhone())
                            .status(1)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return passwordEncoder.encode(request.getPassword())
                            .flatMap(encodedPassword -> {
                                user.setPassword(encodedPassword);
                                return userRepository.save(user);
                            })
                            .map(savedUser -> {
                                log.info("用户注册成功: {}, IP: {}", savedUser.getUsername(), ip);
                                return convertToUserInfoResponse(savedUser);
                            });
                });
    }

    /**
     * 用户登录
     */
    public Mono<UserDTO.LoginResponse> login(UserDTO.LoginRequest request, String ip) {
        return userRepository.findByUsername(request.getUsername())
                .switchIfEmpty(Mono.error(new RuntimeException("用户不存在")))
                .flatMap(user -> {
                    if (user.getStatus() != 1) {
                        return Mono.error(new RuntimeException("账号已被禁用"));
                    }

                    return passwordEncoder.matches(request.getPassword(), user.getPassword())
                            .flatMap(matches -> {
                                if (!matches) {
                                    return Mono.error(new RuntimeException("密码错误"));
                                }

                                // 更新最后登录信息
                                user.setLastLoginTime(LocalDateTime.now());
                                user.setLastLoginIp(ip);

                                return userRepository.save(user)
                                        .map(savedUser -> {
                                            String token = jwtUtil.generateToken(savedUser.getId(), savedUser.getUsername());
                                            log.info("用户登录成功: {}, IP: {}", savedUser.getUsername(), ip);

                                            UserDTO.LoginResponse response = new UserDTO.LoginResponse();
                                            response.setUserId(savedUser.getId());
                                            response.setUsername(savedUser.getUsername());
                                            response.setToken(token);
                                            response.setAvatar(savedUser.getAvatar());
                                            response.setLoginTime(LocalDateTime.now());
                                            return response;
                                        });
                            });
                });
    }

    /**
     * 验证注册请求
     */
    private Mono<Boolean> validateRegisterRequest(UserDTO.RegisterRequest request) {
        return userRepository.countByUsername(request.getUsername())
                .flatMap(count -> {
                    if (count > 0) {
                        return Mono.error(new RuntimeException("用户名已存在"));
                    }

                    if (request.getEmail() != null && !request.getEmail().isEmpty()) {
                        return userRepository.countByEmail(request.getEmail())
                                .flatMap(emailCount -> {
                                    if (emailCount > 0) {
                                        return Mono.error(new RuntimeException("邮箱已被注册"));
                                    }
                                    return validatePhone(request);
                                });
                    }

                    return validatePhone(request);
                });
    }

    /**
     * 验证手机号
     */
    private Mono<Boolean> validatePhone(UserDTO.RegisterRequest request) {
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            return userRepository.countByPhone(request.getPhone())
                    .flatMap(phoneCount -> {
                        if (phoneCount > 0) {
                            return Mono.error(new RuntimeException("手机号已被注册"));
                        }
                        return Mono.just(true);
                    });
        }
        return Mono.just(true);
    }

    /**
     * 转换为用户信息响应
     */
    private UserDTO.UserInfoResponse convertToUserInfoResponse(User user) {
        UserDTO.UserInfoResponse response = new UserDTO.UserInfoResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}