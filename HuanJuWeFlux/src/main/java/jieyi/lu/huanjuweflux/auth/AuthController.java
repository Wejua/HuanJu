package jieyi.lu.huanjuweflux.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<UserDTO.UserInfoResponse>> register(
            @Valid @RequestBody UserDTO.RegisterRequest request,
            ServerWebExchange exchange) {

        String ip = getClientIp(exchange);

        return userService.register(request, ip)
                .map(userInfo -> ResponseEntity.status(HttpStatus.CREATED).body(userInfo))
                .onErrorResume(e -> {
                    log.error("注册失败: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<UserDTO.LoginResponse>> login(
            @Valid @RequestBody UserDTO.LoginRequest request,
            ServerWebExchange exchange) {

        String ip = getClientIp(exchange);

        return userService.login(request, ip)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("登录失败: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(ServerWebExchange exchange) {
        String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getHeaders().getFirst("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "";
        }
        return ip;
    }
}