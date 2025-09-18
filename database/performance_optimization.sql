-- 校园二手交易平台性能优化脚本
-- 版本: v1.0
-- 创建时间: 2024-12-19
-- 说明: 包含索引优化、查询优化、性能监控等

USE `campus_secondhand`;

-- ================================
-- 1. 复合索引优化
-- ================================

-- 商品搜索优化索引
CREATE INDEX `idx_products_search` ON `products` (`category_id`, `status`, `audit_status`, `price`);

-- 用户交易记录查询优化
CREATE INDEX `idx_orders_user_status` ON `orders` (`buyer_id`, `status`, `created_at`);
CREATE INDEX `idx_orders_seller_status` ON `orders` (`seller_id`, `status`, `created_at`);

-- 消息查询优化
CREATE INDEX `idx_messages_conversation` ON `messages` (`sender_id`, `receiver_id`, `created_at`);

-- 收藏查询优化
CREATE INDEX `idx_favorites_user_time` ON `favorites` (`user_id`, `created_at`);

-- 评价查询优化
CREATE INDEX `idx_reviews_product_time` ON `reviews` (`product_id`, `created_at`);

-- ================================
-- 2. JSON字段虚拟列索引
-- ================================

-- 为商品图片数量创建虚拟列和索引
ALTER TABLE `products` 
ADD COLUMN `image_count` INT GENERATED ALWAYS AS (JSON_LENGTH(images)) VIRTUAL,
ADD INDEX `idx_image_count` (`image_count`);

-- ================================
-- 3. 性能监控表
-- ================================

CREATE TABLE `performance_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型',
  `table_name` varchar(50) DEFAULT NULL COMMENT '涉及表名',
  `execution_time` decimal(10,4) NOT NULL COMMENT '执行时间(秒)',
  `rows_affected` int(11) DEFAULT 0 COMMENT '影响行数',
  `query_hash` varchar(64) DEFAULT NULL COMMENT '查询哈希',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_operation_time` (`operation_type`, `created_at`),
  KEY `idx_execution_time` (`execution_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='性能监控日志表';

-- ================================
-- 4. 慢查询分析视图
-- ================================

CREATE VIEW `slow_query_analysis` AS
SELECT 
    operation_type,
    table_name,
    COUNT(*) as query_count,
    AVG(execution_time) as avg_execution_time,
    MAX(execution_time) as max_execution_time,
    SUM(rows_affected) as total_rows_affected
FROM performance_logs 
WHERE execution_time > 1.0  -- 超过1秒的查询
GROUP BY operation_type, table_name
ORDER BY avg_execution_time DESC;

-- ================================
-- 5. 数据库统计信息更新
-- ================================

-- 更新表统计信息（提升查询优化器性能）
ANALYZE TABLE `users`, `products`, `orders`, `categories`, `schools`;

-- ================================
-- 6. 分区表示例（大数据量时使用）
-- ================================

-- 订单表按月分区示例（注释形式）
/*
ALTER TABLE `orders` 
PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
*/

-- ================================
-- 7. 缓存预热存储过程
-- ================================

DELIMITER //
CREATE PROCEDURE `WarmupCache`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE cache_query TEXT;
    DECLARE cache_cursor CURSOR FOR 
        SELECT CONCAT('SELECT COUNT(*) FROM ', table_name) 
        FROM information_schema.tables 
        WHERE table_schema = 'campus_secondhand' 
        AND table_type = 'BASE TABLE';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 预热主要表的缓存
    SELECT COUNT(*) FROM users WHERE deleted = 0;
    SELECT COUNT(*) FROM products WHERE audit_status = 1;
    SELECT COUNT(*) FROM categories WHERE status = 1;
    SELECT COUNT(*) FROM schools WHERE status = 1;
    
    -- 预热热点数据
    SELECT * FROM categories WHERE parent_id = 0 ORDER BY sort;
    SELECT * FROM products WHERE status = 'available' AND audit_status = 1 ORDER BY created_at DESC LIMIT 20;
    
    SELECT '✅ 缓存预热完成' as status;
END //
DELIMITER ;

-- ================================
-- 8. 数据清理存储过程
-- ================================

DELIMITER //
CREATE PROCEDURE `CleanupOldData`(IN days_to_keep INT)
BEGIN
    DECLARE cleanup_date DATETIME;
    SET cleanup_date = DATE_SUB(NOW(), INTERVAL days_to_keep DAY);
    
    -- 清理过期的操作日志
    DELETE FROM operation_logs WHERE created_at < cleanup_date;
    
    -- 清理过期的性能日志
    DELETE FROM performance_logs WHERE created_at < cleanup_date;
    
    -- 清理已删除用户的相关数据（软删除超过指定天数）
    DELETE FROM messages WHERE 
        (sender_id IN (SELECT id FROM users WHERE deleted = 1 AND updated_at < cleanup_date)
         OR receiver_id IN (SELECT id FROM users WHERE deleted = 1 AND updated_at < cleanup_date));
    
    SELECT 
        '✅ 数据清理完成' as status,
        cleanup_date as cleanup_before_date,
        ROW_COUNT() as rows_affected;
END //
DELIMITER ;

-- ================================
-- 9. 性能监控查询
-- ================================

-- 查看表大小和行数
SELECT 
    table_name,
    table_rows,
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size(MB)',
    ROUND((data_length / 1024 / 1024), 2) AS 'Data(MB)',
    ROUND((index_length / 1024 / 1024), 2) AS 'Index(MB)'
FROM information_schema.tables 
WHERE table_schema = 'campus_secondhand'
ORDER BY (data_length + index_length) DESC;

-- 查看索引使用情况
SELECT 
    table_name,
    index_name,
    column_name,
    cardinality,
    sub_part,
    nullable
FROM information_schema.statistics 
WHERE table_schema = 'campus_secondhand'
ORDER BY table_name, index_name;

-- ================================
-- 10. 优化建议查询
-- ================================

-- 检查未使用的索引（需要结合实际查询日志分析）
SELECT 
    s.table_name,
    s.index_name,
    s.column_name,
    '可能未使用的索引，建议分析后删除' as suggestion
FROM information_schema.statistics s
WHERE s.table_schema = 'campus_secondhand'
  AND s.index_name != 'PRIMARY'
  AND s.index_name NOT IN (
    'uk_phone', 'idx_school_id', 'idx_student_id', 'idx_created_at',
    'idx_name', 'idx_province_city', 'idx_parent_id', 'idx_sort'
  );

-- 性能优化完成提示
SELECT 
    '🚀 性能优化脚本执行完成' as message,
    '✅ 复合索引已创建' as indexes_status,
    '✅ 监控表已就绪' as monitoring_status,
    '✅ 优化存储过程已创建' as procedures_status,
    '💡 建议定期执行性能分析' as recommendation;

/*
使用建议：
1. 定期执行 CALL WarmupCache(); 预热缓存
2. 每周执行 CALL CleanupOldData(90); 清理90天前的数据
3. 监控 slow_query_analysis 视图，优化慢查询
4. 根据实际业务场景调整索引策略
5. 在生产环境中谨慎使用分区表
*/