#!/bin/zsh
# 用来手动创建数据库
mysql -u root -p <<EOF
CREATE DATABASE IF NOT EXISTS \`huan_ju_flux\`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- 显示所有数据库（确认创建成功）
SELECT '----- 所有数据库列表 -----' as '';
SHOW DATABASES;

-- 切换到新数据库
USE \`huan_ju_flux\`;

-- 显示当前数据库
SELECT '----- 当前数据库 -----' as '';
SELECT DATABASE() as '';

-- 显示当前数据库中的表
SELECT '----- 数据库中的表 -----' as '';

-- 显示当前数据库中的表（应该为空）
SHOW TABLES;
EOF