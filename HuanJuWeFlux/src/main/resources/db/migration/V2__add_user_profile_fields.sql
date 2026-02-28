/*
 * Flyway 迁移脚本 V2
 * 作者: jieyi.lu
 * 日期: 2024-02-01
 * 描述: 增加用户个人资料字段
 */

USE `huan_ju_flux`;

-- 增加用户昵称字段
ALTER TABLE `user`
ADD COLUMN `nickname` VARCHAR(50) AFTER `username`,
ADD COLUMN `gender` TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女' AFTER `avatar`,
ADD COLUMN `birthday` DATE AFTER `gender`;

-- 增加备注
ALTER TABLE `user`
    MODIFY COLUMN `nickname` VARCHAR(50) COMMENT '用户昵称';