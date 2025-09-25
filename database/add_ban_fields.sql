-- 添加用户封禁相关字段
-- 执行时间: 2025-01-XX
-- 描述: 为users表添加封禁相关字段，支持用户封禁功能

USE campus_secondhand;

-- 添加封禁原因字段
ALTER TABLE users ADD COLUMN ban_reason VARCHAR(500) NULL COMMENT '封禁原因' AFTER verify_reason;

-- 添加封禁结束时间字段
ALTER TABLE users ADD COLUMN ban_end_time DATETIME NULL COMMENT '封禁结束时间' AFTER ban_reason;

-- 添加索引以提高查询性能
ALTER TABLE users ADD INDEX idx_ban_end_time (ban_end_time);

-- 验证字段是否添加成功
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_COMMENT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'campus_secondhand' 
AND TABLE_NAME = 'users' 
AND COLUMN_NAME IN ('ban_reason', 'ban_end_time');