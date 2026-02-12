package security;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private String jwtSecret;
}
