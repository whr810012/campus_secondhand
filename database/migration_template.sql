-- 数据库迁移脚本模板
-- 版本: v2.1
-- 迁移类型: [ADD_TABLE|ALTER_TABLE|ADD_INDEX|DROP_INDEX|ADD_COLUMN|DROP_COLUMN|UPDATE_DATA]
-- 描述: [具体的变更描述]
-- 创建时间: YYYY-MM-DD
-- 作者: [开发者姓名]

-- 迁移前检查
SELECT 
    SCHEMA_NAME as database_name,
    DEFAULT_CHARACTER_SET_NAME as charset,
    DEFAULT_COLLATION_NAME as collation
FROM information_schema.SCHEMATA 
WHERE SCHEMA_NAME = 'campus_secondhand';

-- 备份重要数据（如果需要）
-- CREATE TABLE backup_table_name AS SELECT * FROM original_table_name;

-- 开始事务
START TRANSACTION;

-- 迁移脚本内容
-- 示例：添加新字段
/*
ALTER TABLE `users` 
ADD COLUMN `new_field` VARCHAR(100) DEFAULT NULL COMMENT '新字段描述' 
AFTER `existing_field`;
*/

-- 示例：创建新索引
/*
CREATE INDEX `idx_new_field` ON `users` (`new_field`);
*/

-- 示例：更新数据
/*
UPDATE `users` 
SET `new_field` = 'default_value' 
WHERE `new_field` IS NULL;
*/

-- 验证迁移结果
-- SELECT COUNT(*) FROM table_name WHERE condition;

-- 提交事务
COMMIT;

-- 迁移完成提示
SELECT 
    '✅ 数据库迁移完成' as status,
    NOW() as completion_time,
    'v2.1' as version;

-- 回滚脚本（注释形式保存）
/*
-- 如果需要回滚，执行以下语句：
-- ALTER TABLE `users` DROP COLUMN `new_field`;
-- DROP INDEX `idx_new_field` ON `users`;
*/