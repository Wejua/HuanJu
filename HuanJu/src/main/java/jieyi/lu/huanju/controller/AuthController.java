package jieyi.lu.huanju.controller;

import jieyi.lu.huanju.dto.LoginRequest;
import jieyi.lu.huanju.dto.LoginResponse;
import jakarta.validation.Valid;
import jieyi.lu.huanju.dto.RegisterRequest;
import jieyi.lu.huanju.dto.RegisterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import jieyi.lu.huanju.service.AuthService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 不指定接收类型（consumes）和产生类型（produces）就是默认都可以，但是要引入额外的库，默认只支持 application/json
    @PostMapping (path = "/login", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) { // @RequestBody是将请求体中的 JSON 转换成 LoginRequest 类型
        // 检查是否有验证错误，对应@Valid，如果有错误会返回该属性上的注解中的提示
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage) // 遍历调用 ObjectError 的 getDefaultMessage 方法
                    .collect(Collectors.joining("; ")); // 拼接 ；合成字符串
            return ResponseEntity.badRequest().body(errorMessage);
        }
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest ) {
        try {
            RegisterResponse response = authService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("login/test")
    public String test() {
        return "登录接口测试成功！";
    }
}
