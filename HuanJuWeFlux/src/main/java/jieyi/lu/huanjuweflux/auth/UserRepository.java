package jieyi.lu.huanjuweflux.auth;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {

    /**
     * 根据用户名查询用户
     */
    Mono<User> findByUsername(String username);

    /**
     * 根据邮箱查询用户
     */
    Mono<User> findByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    Mono<User> findByPhone(String phone);

    /**
     * 检查用户名是否存在
     */
    @Query("SELECT COUNT(*) FROM user WHERE username = :username")
    Mono<Long> countByUsername(String username);

    /**
     * 检查邮箱是否存在
     */
    @Query("SELECT COUNT(*) FROM user WHERE email = :email")
    Mono<Long> countByEmail(String email);

    /**
     * 检查手机号是否存在
     */
    @Query("SELECT COUNT(*) FROM user WHERE phone = :phone")
    Mono<Long> countByPhone(String phone);

    /**
     * 更新最后登录信息
     */
    @Modifying
    @Query("UPDATE user SET last_login_time = :lastLoginTime, last_login_ip = :lastLoginIp WHERE id = :userId")
    Mono<Integer> updateLoginInfo(Long userId, LocalDateTime lastLoginTime, String lastLoginIp);
}