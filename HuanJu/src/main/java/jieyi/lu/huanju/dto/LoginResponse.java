package jieyi.lu.huanju.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class LoginResponse {
    @NonNull
    private String token;

    private String type = "Bearer";

    @NonNull
    private Long id;

    @NonNull
    private String username;

    @NonNull
    private String email;

    @NonNull
    private String role;
}
