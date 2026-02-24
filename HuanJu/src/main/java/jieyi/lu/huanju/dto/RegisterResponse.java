package jieyi.lu.huanju.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private Long id;
    private String username;
    private String email;
    private String nickname;
    private String role;  // 默认角色，如 "USER"
    private LocalDateTime createdAt;
    private String message;
}
