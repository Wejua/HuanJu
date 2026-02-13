package jieyi.lu.huanju.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

// 多个地方使用这个Bean也是使用同一个单例，因为默认是@Scope("singleton")
// @Scope("prototype")  // 每次注入都创建新对象
// @Scope("request")    // 每个HTTP请求一个实例
// @Scope("session")    // 每个用户会话一个实例
@Component  // 没有写构造方法，Java 默认会生成无参构造方法
@Scope("singleton") // 可以省略，默认就是这个
public class JwtUtil {

    @Value("${jwt.secret:mySecretKey}") // 从配置文件中读取jwt.secret, 找不到配置时用 mySecretKey
    private String secret;

    @Value("${jwt.expiration:86400000}") // 默认24小时
    private Long expiration;

    @Value("${jwt.issuer:HuanJu}")
    private String issuer;

    public String generateToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(secret));
    }

    public String extractUsername(String token) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            DecodedJWT decodedJWT = decodeToken(token);
            String username = decodedJWT.getSubject();
            Date expiresAt = decodedJWT.getExpiresAt();

            return username.equals(userDetails.getUsername()) && expiresAt.after(new Date());
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private DecodedJWT decodeToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
        return verifier.verify(token);
    }
}
