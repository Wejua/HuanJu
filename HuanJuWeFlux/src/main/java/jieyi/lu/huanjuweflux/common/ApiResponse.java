package jieyi.lu.huanjuweflux.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {
    @JsonProperty("isSuccess")
    private boolean success;
    private T data;
    private String message;
    private int status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setData(data);
        response.setStatus(200);
        response.setMessage("成功");
        response.setTimestamp(LocalDateTime.now());
        return response;
    }

    public static <T> ApiResponse<T> error(String message, int status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage(message);
        response.setStatus(status);
        response.setTimestamp(LocalDateTime.now());
        return response;
    }
}