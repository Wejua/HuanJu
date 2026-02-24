package jieyi.lu.huanju.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* MySQL数据库原理

# InnoDB 存储单位层级：
MySQL 数据库
    ↓
表空间 (.ibd 文件)
    ↓
区 (Extent) = 1MB = 64个页
    ↓
页 (Page) = 16KB ← 这是最核心的单位！
    ↓
行 (Row) = 一条记录
    ↓
字段 (Column)

# 页的物理结构
一个页 = 16KB = 16384 字节
┌─────────────────────────────────────┐
│ 页头 (38字节)                        │
│ - 页号、LSN、校验和、页类型等         │
├─────────────────────────────────────┤
│ Infimum + Supremum (26字节)          │
│ - 最小记录和最大记录的虚拟行          │
├─────────────────────────────────────┤
│ 用户记录区域 (存储实际数据，没有字段名)           │
│ ┌─────────────────────────────────┐ │
│ │ 记录1: [1][张三]...      │ │
│ │ 记录2: [2][李四]...      │ │
│ │ 记录3: [3][王五]...      │ │
│ │ ...                             │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ 页目录 (按槽分组，加快查找)          │
├─────────────────────────────────────┤
│ 页尾 (8字节)                         │
│ - 校验和                             │
└─────────────────────────────────────┘
*/

@Data
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String detail;

    @Column(nullable = false)
    private String location;

    @Column(name = "start_time")  // 活动开始时间
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // 多对多关系通过组合 users 表中数据的 id 和 activities 表中数据的 id 作为一条数据存储并且保持唯一，当要查询用户参加了哪些活动，只需查询中间表中包含这个用户 id 的所有数据就行了。
    @ManyToMany // 声明这是「多对多」关系, 告诉 JPA，「这两个实体需要一张中间表来关联」
    @JoinTable(
            name = "activity_attendees", // 中间表的名字叫 activity_attendees
            joinColumns = @JoinColumn(name = "activity_id"), // 当前实体（Activity）在中间表中的外键列名叫 activity_id
            inverseJoinColumns = @JoinColumn(name = "user_id") // 对方实体（User）在中间表中的外键列名叫 user_id
    )
    private List<User> attendees = new ArrayList<>();

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Enumerated(EnumType.STRING)  // 存字符串 "UPCOMING" 而不是数字
    private ActivityStatus status = ActivityStatus.UPCOMING;
}
