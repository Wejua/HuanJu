package jieyi.lu.huanjuweflux.RTC;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String msgId;           // 消息ID
    private Integer type;            // 1-私聊 2-群聊
    private Long fromUserId;         // 发送者ID
    private Long toId;               // 接收者ID(用户ID或群组ID)
    private String content;          // 消息内容
    private Integer contentType;     // 1-文本 2-图片 3-文件
    private Integer status;          // 0-发送中 1-已发送 2-已送达 3-已读
    private LocalDateTime createdAt; // 发送时间
    private LocalDateTime updatedAt; // 更新时间
}
