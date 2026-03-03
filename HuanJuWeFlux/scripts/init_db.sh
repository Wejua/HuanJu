#!/bin/zsh

set -e # 出错停止

# 颜色输出
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

# 密码
MYSQL_PASSWORD="password"
MONGO_PASSWORD="password"
REDIS_PASSWORD="password"

echo "========================================="
echo "开始初始化数据库"
echo "========================================="

# 1. 初始化 MySQL
echo -e "\n${GREEN}1. 初始化 MySQL${NC}"
mysql -u root -p${MYSQL_PASSWORD} <<EOF

-- 创建数据库
CREATE DATABASE IF NOT EXISTS huan_ju_flux
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
