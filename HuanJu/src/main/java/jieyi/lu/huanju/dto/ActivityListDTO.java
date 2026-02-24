package jieyi.lu.huanju.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jieyi.lu.huanju.entity.Activity;
import jieyi.lu.huanju.entity.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// 这个类的作用，解决以下问题：
// 循环依赖 - attendees 里的 User 又有 activities，可能死循环
// 暴露敏感信息 - 返回了不该返回的字段
// 性能差 - 查活动列表顺便把所有参加者都查出来了
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityListDTO {
    private Long id;
    private String title;
    private String summary;  // 摘要（详情截取前100字）
    private String location;
    // 用注解自动格式化
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private ActivityStatus status;      // 枚举值 UPCOMING
    private String statusText;  // 中文描述 "即将开始"
    private Integer attendeeCount;  // 参加人数

    // 用于 JPQL 构造的构造方法
    public ActivityListDTO(Long id, String title, String summary,
                           String location, LocalDateTime startTime, LocalDateTime endTime,
                           Integer maxParticipants, ActivityStatus status, Integer attendeeCount) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxParticipants = maxParticipants;
        this.status = status;
        this.attendeeCount = attendeeCount;
    }
}