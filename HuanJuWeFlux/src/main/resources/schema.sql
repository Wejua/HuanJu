
-- 切换到数据库
USE `huan_ju_flux`;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `nickname` VARCHAR(100),
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`),
    KEY `idx_status` (`status`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 群组表
CREATE TABLE IF NOT EXISTS `group` (
                                       `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '群组ID',
                                       `name` VARCHAR(100) NOT NULL COMMENT '群组名称',
    `description` VARCHAR(500) COMMENT '群组描述',
    `avatar` VARCHAR(500) COMMENT '群头像URL',
    `owner_id` BIGINT NOT NULL COMMENT '群主ID',
    `max_members` INT DEFAULT 200 COMMENT '最大成员数',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-解散',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`owner_id`) REFERENCES `user`(`id`),
    KEY `idx_owner` (`owner_id`),
    KEY `idx_status` (`status`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群组表';

-- 群成员表
CREATE TABLE IF NOT EXISTS `group_member` (
                                              `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
                                              `group_id` BIGINT NOT NULL COMMENT '群组ID',
                                              `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                              `role` TINYINT DEFAULT 1 COMMENT '角色：1-成员 2-管理员 3-群主',
                                              `nickname` VARCHAR(100) COMMENT '群昵称',
    `mute_end_time` DATETIME COMMENT '禁言截止时间',
    `joined_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    FOREIGN KEY (`group_id`) REFERENCES `group`(`id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_group_user` (`group_id`, `user_id`),
    KEY `idx_user` (`user_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='群成员表';

-- 好友关系表
CREATE TABLE IF NOT EXISTS `friend` (
                                        `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
                                        `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                        `friend_id` BIGINT NOT NULL COMMENT '好友ID',
                                        `remark` VARCHAR(100) COMMENT '好友备注',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待确认 1-已确认 2-已拉黑 3-已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`friend_id`) REFERENCES `user`(`id`),
    UNIQUE KEY `uk_user_friend` (`user_id`, `friend_id`),
    KEY `idx_friend` (`friend_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友关系表';

-- 好友请求表
CREATE TABLE IF NOT EXISTS `friend_request` (
                                                `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
                                                `from_user_id` BIGINT NOT NULL COMMENT '请求发起用户ID',
                                                `to_user_id` BIGINT NOT NULL COMMENT '目标用户ID',
                                                `message` VARCHAR(255) COMMENT '验证消息',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-已同意 2-已拒绝 3-已忽略',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`from_user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`to_user_id`) REFERENCES `user`(`id`),
    KEY `idx_from_user` (`from_user_id`),
    KEY `idx_to_user` (`to_user_id`),
    KEY `idx_status` (`status`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友请求表';

-- 离线消息表
CREATE TABLE IF NOT EXISTS `offline_message` (
                                                 `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
                                                 `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                                 `msg_id` VARCHAR(64) NOT NULL COMMENT '消息ID',
    `from_user_id` BIGINT NOT NULL COMMENT '发送者ID',
    `msg_type` TINYINT NOT NULL COMMENT '消息类型：1-私聊 2-群聊',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `content_type` TINYINT DEFAULT 1 COMMENT '内容类型：1-文本 2-图片 3-文件',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
    FOREIGN KEY (`from_user_id`) REFERENCES `user`(`id`),
    KEY `idx_user_created` (`user_id`, `created_at`),
    KEY `idx_msg_id` (`msg_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='离线消息表';