package jieyi.lu.huanjuweflux.auth;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class PasswordEncoder {

    /**
     * 加密密码（使用SHA-256 + Base64）
     */
    public Mono<String> encode(String rawPassword) {
        return Mono.fromCallable(() -> {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(rawPassword.getBytes());
                return Base64.getEncoder().encodeToString(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("密码加密失败", e);
            }
        });
    }

    /**
     * 验证密码
     */
    public Mono<Boolean> matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword)
                .map(encoded -> encoded.equals(encodedPassword));
    }
}