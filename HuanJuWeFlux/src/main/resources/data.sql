USE `huan_ju_flux`;

-- 插入测试数据（如果需要）
INSERT IGNORE INTO `user` (`username`, `password`, `email`, `phone`) VALUES
    ('admin2', 'adminpassword2', 'admin@example0.com', '13800138001');

-- 插入测试用户（使用您已有的用户表结构）
INSERT IGNORE INTO `user` (`username`, `nickname`, `password`, `email`, `phone`, `status`) VALUES
                                                                                        ('user1', '用户1', '$2a$10$X7oVY5qX5X5X5X5X5X5X5u', 'user1@example.com', '13800000001', 1),
                                                                                        ('user2', '用户2', '$2a$10$X7oVY5qX5X5X5X5X5X5X5u', 'user2@example.com', '13800000002', 1),
                                                                                        ('user3', '用户3', '$2a$10$X7oVY5qX5X5X5X5X5X5X5u', 'user3@example.com', '13800000003', 1),
                                                                                        ('admin', '管理员', '$2a$10$X7oVY5qX5X5X5X5X5X5X5u', 'admin@example.com', '13800000000', 1);

-- 插入测试群组
INSERT IGNORE INTO `group` (`name`, `description`, `owner_id`, `max_members`, `status`) VALUES
                                                                                     ('技术交流群', '讨论技术问题', 1, 100, 1),
                                                                                     ('闲聊群', '随便聊聊', 2, 50, 1);

-- 插入群成员
INSERT IGNORE INTO `group_member` (`group_id`, `user_id`, `role`, `nickname`) VALUES
                                                                           (1, 1, 3, '群主-技术'),  -- 群主
                                                                           (1, 2, 2, '管理员-小张'), -- 管理员
                                                                           (1, 3, 1, '小李'),       -- 成员
                                                                           (1, 4, 1, '小王'),       -- 成员
                                                                           (2, 2, 3, '群主-闲聊'),  -- 群主
                                                                           (2, 1, 1, '游客');       -- 成员

-- 插入好友关系
INSERT IGNORE INTO `friend` (`user_id`, `friend_id`, `remark`, `status`) VALUES
                                                                      (1, 2, '好朋友', 1),   -- 已确认
                                                                      (1, 3, '新朋友', 0),   -- 待确认
                                                                      (2, 3, '老铁', 1);     -- 已确认

-- 插入好友请求
INSERT IGNORE INTO `friend_request` (`from_user_id`, `to_user_id`, `message`, `status`) VALUES
                                                                                     (1, 3, '加个好友吧', 0),      -- 待处理
                                                                                     (2, 4, '通过一下', 1),        -- 已同意
                                                                                     (3, 1, '你好', 2);            -- 已拒绝