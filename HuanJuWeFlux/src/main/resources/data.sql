USE `huan_ju_flux`;

-- 插入测试数据（如果需要）
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `phone`) VALUES
    ('admin2', 'adminpassword2', 'admin@example.com', '13800138001');

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