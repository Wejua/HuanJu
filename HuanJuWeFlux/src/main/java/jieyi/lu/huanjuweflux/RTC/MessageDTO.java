package jieyi.lu.huanjuweflux.RTC;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String msgId;           // 消息ID
    private Integer type;            // 1-私聊 2-群聊
    private Integer action;          // 1-发送消息 2-心跳 3-确认接收 4-确认已读
    private Long fromUserId;         // 发送者ID
    private Long toId;               // 接收者ID(用户ID或群组ID)
    private String content;          // 消息内容
    private Integer contentType;     // 1-文本 2-图片 3-文件
    private LocalDateTime timestamp; // 时间戳

    // 业务状态码
    private Integer code;            // 200-成功 其他-失败
    private String message;          // 响应消息
}
