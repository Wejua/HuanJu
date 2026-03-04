package jieyi.lu.huanjuweflux.auth;

import jakarta.validation.Valid;
import jieyi.lu.huanjuweflux.common.ApiResponse;
import jieyi.lu.huanjuweflux.common.UserDTO;
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
    @ResponseStatus(HttpStatus.CREATED) // 可以省略，优先级比方法体里获取的低
    public Mono<ResponseEntity<ApiResponse<?>>> register(
            @Valid @RequestBody UserDTO.RegisterRequest request,
            ServerWebExchange exchange) { // ServerWebExchange 是 WebFlux 的上下文，可获取请求信息

        String ip = getClientIp(exchange);

        return userService.register(request, ip)
                .map(userInfo -> ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userInfo)));
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
                .map(ResponseEntity::ok);
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