package jieyi.lu.huanju.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users") // 指定数据库表名为 users
@Entity
public class User {
    @Id // JPA 中的注解，标记这是主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 数据库自动生成递增的唯一值
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 20)
    private String role = "USER";

    @Column(nullable = false)
    private Boolean enabled = true;

    @CreationTimestamp // Hibernate 提供的注解，在 INSERT 时自动生成当前时间戳
    @Column(name = "created_at", updatable = false) // updatable 表示 UPDATE 时忽略该字段，值不会改变
    private LocalDateTime createdAt;

    @UpdateTimestamp // 更新就填入当时的时间戳
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 50)
    private String nickname;
}
