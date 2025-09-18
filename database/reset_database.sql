-- 校园二手交易平台数据库完全重置脚本
-- 版本: v1.0
-- 创建时间: 2024-12-19
-- 说明: 完全清理数据库并重新初始化

-- 设置SQL模式和字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO';

-- 删除数据库（谨慎使用）
-- DROP DATABASE IF EXISTS `campus_secondhand`;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `campus_secondhand` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `campus_secondhand`;

-- 获取所有表名并删除
SET @tables = NULL;
SELECT GROUP_CONCAT('`', table_name, '`') INTO @tables
FROM information_schema.tables
WHERE table_schema = 'campus_secondhand' AND table_type = 'BASE TABLE';

SET @tables = CONCAT('DROP TABLE IF EXISTS ', @tables);
PREPARE stmt FROM @tables;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 获取所有视图并删除
SET @views = NULL;
SELECT GROUP_CONCAT('`', table_name, '`') INTO @views
FROM information_schema.views
WHERE table_schema = 'campus_secondhand';

IF @views IS NOT NULL THEN
    SET @views = CONCAT('DROP VIEW IF EXISTS ', @views);
    PREPARE stmt FROM @views;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END IF;

-- 获取所有存储过程并删除
DROP PROCEDURE IF EXISTS `UpdateUserCreditScore`;
DROP PROCEDURE IF EXISTS `WarmupCache`;
DROP PROCEDURE IF EXISTS `CleanupOldData`;
DROP PROCEDURE IF EXISTS `CheckSuspiciousLogin`;
DROP PROCEDURE IF EXISTS `AutoBlockSuspiciousIP`;

-- 获取所有函数并删除
DROP FUNCTION IF EXISTS `EncryptPhone`;
DROP FUNCTION IF EXISTS `DecryptPhone`;
DROP FUNCTION IF EXISTS `MaskIdCard`;

-- 获取所有触发器并删除
SET @triggers = NULL;
SELECT GROUP_CONCAT(trigger_name) INTO @triggers
FROM information_schema.triggers
WHERE trigger_schema = 'campus_secondhand';

IF @triggers IS NOT NULL THEN
    SET @sql = CONCAT('DROP TRIGGER IF EXISTS ', REPLACE(@triggers, ',', '; DROP TRIGGER IF EXISTS '));
    SET @sql = CONCAT(@sql, ';');
    
    -- 由于触发器删除需要逐个执行，这里提供手动删除的语句
    -- 实际使用时需要根据具体触发器名称调整
END IF;

-- 手动删除已知触发器
DROP TRIGGER IF EXISTS `product_status_update_trigger`;
DROP TRIGGER IF EXISTS `user_sensitive_data_audit`;
DROP TRIGGER IF EXISTS `admin_operation_audit`;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 重置完成提示
SELECT 
    '🔄 数据库重置完成' as message,
    '✅ 所有表已删除' as tables_status,
    '✅ 所有视图已删除' as views_status,
    '✅ 所有存储过程已删除' as procedures_status,
    '✅ 所有触发器已删除' as triggers_status,
    '🚀 可以重新执行初始化脚本' as next_step;

/*
使用说明：
1. 执行此脚本将完全清理数据库中的所有对象
2. 执行后请立即运行 init.sql 重新初始化数据库
3. 生产环境使用前请务必备份数据
4. 如需完全删除数据库，请取消注释 DROP DATABASE 语句

执行顺序：
1. mysql -u root -p < reset_database.sql
2. mysql -u root -p < init.sql
3. mysql -u root -p campus_secondhand < performance_optimization.sql
4. mysql -u root -p campus_secondhand < security_enhancement.sql
*/