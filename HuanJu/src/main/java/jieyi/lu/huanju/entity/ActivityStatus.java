package jieyi.lu.huanju.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;


public enum ActivityStatus {
    UPCOMING("即将开始"),
    ONGOING("进行中"),
    ENDED("已结束"),
    CANCELLED("已取消");

    private final String description;

    ActivityStatus(String description) {
        this.description = description;
    }

    @JsonValue  // 序列化时返回这个对象  枚举增强
    public Map<String, String> toJson() {
        return Map.of(
                "status", this.name(),
                "statusDescription", this.description
        );
    }

    public String getDescription() {
        return description;
    }
}
