/*
 * Flyway 迁移脚本 V3
 * 作者: jieyi.lu
 * 日期: 2024-03-01
 * 描述: 插入默认管理员用户（仅用于开发环境）
 * 注意：密码需要在应用启动后通过代码更新
 */

USE `huan_ju_flux`;

-- 插入默认管理员（如果不存在）
INSERT IGNORE INTO `user` (
    `username`,
    `password`,
    `email`,
    `phone`,
    `avatar`,
    `status`,
    `created_at`
) VALUES (
    'admin',
    'FLYWAY_PLACEHOLDER', -- 占位符，实际密码会在应用启动后更新
    'admin@huanju.com',
    '13800138000',
    'https://example.com/default-avatar.png',
    1,
    NOW()
);