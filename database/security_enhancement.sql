-- 校园二手交易平台安全加固脚本
-- 版本: v1.0
-- 创建时间: 2024-12-19
-- 说明: 包含数据加密、访问控制、审计日志等安全措施

USE `campus_secondhand`;

-- ================================
-- 1. 敏感数据加密函数
-- ================================

-- 手机号加密函数
DELIMITER //
CREATE FUNCTION `EncryptPhone`(phone_number VARCHAR(20)) 
RETURNS VARCHAR(255)
READS SQL DATA
DETERMINISTIC
BEGIN
    -- 使用AES加密，实际项目中密钥应从配置文件读取
    RETURN AES_ENCRYPT(phone_number, 'campus_secret_key_2024');
END //
DELIMITER ;

-- 手机号解密函数
DELIMITER //
CREATE FUNCTION `DecryptPhone`(encrypted_phone VARBINARY(255)) 
RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    RETURN AES_DECRYPT(encrypted_phone, 'campus_secret_key_2024');
END //
DELIMITER ;

-- 身份证号脱敏函数
DELIMITER //
CREATE FUNCTION `MaskIdCard`(id_card VARCHAR(18)) 
RETURNS VARCHAR(18)
READS SQL DATA
DETERMINISTIC
BEGIN
    IF id_card IS NULL OR LENGTH(id_card) < 6 THEN
        RETURN id_card;
    END IF;
    RETURN CONCAT(LEFT(id_card, 3), '***********', RIGHT(id_card, 4));
END //
DELIMITER ;

-- ================================
-- 2. 安全审计表
-- ================================

CREATE TABLE `security_audit_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `action_type` varchar(50) NOT NULL COMMENT '操作类型',
  `resource_type` varchar(50) DEFAULT NULL COMMENT '资源类型',
  `resource_id` varchar(100) DEFAULT NULL COMMENT '资源ID',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text DEFAULT NULL COMMENT '用户代理',
  `request_data` json DEFAULT NULL COMMENT '请求数据',
  `response_status` int(11) DEFAULT NULL COMMENT '响应状态码',
  `risk_level` tinyint(1) DEFAULT 0 COMMENT '风险级别：0-低，1-中，2-高',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_action` (`user_id`, `action_type`),
  KEY `idx_risk_level` (`risk_level`, `created_at`),
  KEY `idx_ip_address` (`ip_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='安全审计日志表';

-- ================================
-- 3. 登录安全表
-- ================================

CREATE TABLE `login_security` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL COMMENT '登录手机号',
  `ip_address` varchar(50) NOT NULL,
  `login_status` tinyint(1) NOT NULL COMMENT '登录状态：0-失败，1-成功',
  `failure_reason` varchar(100) DEFAULT NULL COMMENT '失败原因',
  `user_agent` text DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL COMMENT '登录地点',
  `device_fingerprint` varchar(255) DEFAULT NULL COMMENT '设备指纹',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_ip` (`user_id`, `ip_address`),
  KEY `idx_phone_status` (`phone`, `login_status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录安全记录表';

-- ================================
-- 4. IP黑名单表
-- ================================

CREATE TABLE `ip_blacklist` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip_address` varchar(50) NOT NULL,
  `reason` varchar(255) DEFAULT NULL COMMENT '拉黑原因',
  `block_type` tinyint(1) DEFAULT 1 COMMENT '封禁类型：1-临时，2-永久',
  `expire_time` datetime DEFAULT NULL COMMENT '过期时间',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ip_address` (`ip_address`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='IP黑名单表';

-- ================================
-- 5. 安全检查存储过程
-- ================================

-- 检查异常登录
DELIMITER //
CREATE PROCEDURE `CheckSuspiciousLogin`()
BEGIN
    -- 检查短时间内多次失败登录
    SELECT 
        phone,
        ip_address,
        COUNT(*) as failure_count,
        MAX(created_at) as last_attempt
    FROM login_security 
    WHERE login_status = 0 
      AND created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
    GROUP BY phone, ip_address
    HAVING failure_count >= 5
    ORDER BY failure_count DESC;
    
    -- 检查异地登录
    SELECT DISTINCT
        u.phone,
        u.nickname,
        ls.ip_address,
        ls.location,
        ls.created_at
    FROM login_security ls
    JOIN users u ON ls.user_id = u.id
    WHERE ls.login_status = 1
      AND ls.created_at >= DATE_SUB(NOW(), INTERVAL 24 HOUR)
      AND ls.location IS NOT NULL
      AND ls.location != (
          SELECT location FROM login_security ls2 
          WHERE ls2.user_id = ls.user_id 
            AND ls2.login_status = 1 
            AND ls2.created_at < ls.created_at
          ORDER BY ls2.created_at DESC LIMIT 1
      );
END //
DELIMITER ;

-- 自动封禁可疑IP
DELIMITER //
CREATE PROCEDURE `AutoBlockSuspiciousIP`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE suspicious_ip VARCHAR(50);
    DECLARE failure_count INT;
    
    DECLARE ip_cursor CURSOR FOR 
        SELECT ip_address, COUNT(*) as failures
        FROM login_security 
        WHERE login_status = 0 
          AND created_at >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
        GROUP BY ip_address
        HAVING failures >= 10;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN ip_cursor;
    
    read_loop: LOOP
        FETCH ip_cursor INTO suspicious_ip, failure_count;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 检查IP是否已在黑名单中
        IF NOT EXISTS (SELECT 1 FROM ip_blacklist WHERE ip_address = suspicious_ip) THEN
            INSERT INTO ip_blacklist (ip_address, reason, block_type, expire_time)
            VALUES (suspicious_ip, CONCAT('自动封禁：1小时内失败登录', failure_count, '次'), 1, DATE_ADD(NOW(), INTERVAL 24 HOUR));
        END IF;
    END LOOP;
    
    CLOSE ip_cursor;
    
    SELECT CONCAT('已处理 ', ROW_COUNT(), ' 个可疑IP') as result;
END //
DELIMITER ;

-- ================================
-- 6. 数据脱敏视图
-- ================================

-- 用户信息脱敏视图（供前端展示使用）
CREATE VIEW `users_masked_view` AS
SELECT 
    id,
    CONCAT(LEFT(phone, 3), '****', RIGHT(phone, 4)) as phone_masked,
    nickname,
    avatar,
    CASE 
        WHEN student_id IS NOT NULL THEN CONCAT(LEFT(student_id, 2), '***', RIGHT(student_id, 2))
        ELSE NULL 
    END as student_id_masked,
    school_id,
    CASE 
        WHEN real_name IS NOT NULL THEN CONCAT(LEFT(real_name, 1), '*', RIGHT(real_name, 1))
        ELSE NULL 
    END as real_name_masked,
    MaskIdCard(id_card) as id_card_masked,
    gender,
    role,
    status,
    verify_status,
    credit_score,
    trade_count,
    good_rate,
    created_at
FROM users
WHERE deleted = 0;

-- ================================
-- 7. 安全触发器
-- ================================

-- 用户敏感信息变更审计触发器
DELIMITER //
CREATE TRIGGER `user_sensitive_data_audit`
AFTER UPDATE ON `users`
FOR EACH ROW
BEGIN
    IF OLD.phone != NEW.phone OR OLD.id_card != NEW.id_card OR OLD.real_name != NEW.real_name THEN
        INSERT INTO security_audit_logs (user_id, action_type, resource_type, resource_id, risk_level)
        VALUES (NEW.id, 'UPDATE_SENSITIVE_DATA', 'users', NEW.id, 2);
    END IF;
END //
DELIMITER ;

-- 管理员操作审计触发器
DELIMITER //
CREATE TRIGGER `admin_operation_audit`
AFTER INSERT ON `operation_logs`
FOR EACH ROW
BEGIN
    IF NEW.operation_type IN ('DELETE_USER', 'BLOCK_USER', 'AUDIT_PRODUCT') THEN
        INSERT INTO security_audit_logs (user_id, action_type, resource_type, resource_id, risk_level)
        VALUES (NEW.operator_id, NEW.operation_type, NEW.target_type, NEW.target_id, 1);
    END IF;
END //
DELIMITER ;

-- ================================
-- 8. 权限控制建议
-- ================================

-- 创建只读用户（用于报表查询）
-- CREATE USER 'campus_readonly'@'%' IDENTIFIED BY 'ReadOnly@2024';
-- GRANT SELECT ON campus_secondhand.* TO 'campus_readonly'@'%';

-- 创建应用用户（用于应用程序连接）
-- CREATE USER 'campus_app'@'%' IDENTIFIED BY 'AppUser@2024';
-- GRANT SELECT, INSERT, UPDATE ON campus_secondhand.* TO 'campus_app'@'%';
-- GRANT DELETE ON campus_secondhand.messages TO 'campus_app'@'%';
-- GRANT DELETE ON campus_secondhand.favorites TO 'campus_app'@'%';

-- 创建管理员用户（用于数据维护）
-- CREATE USER 'campus_admin'@'%' IDENTIFIED BY 'AdminUser@2024';
-- GRANT ALL PRIVILEGES ON campus_secondhand.* TO 'campus_admin'@'%';

-- ================================
-- 9. 安全配置检查
-- ================================

-- 检查弱密码用户
SELECT 
    id,
    phone,
    nickname,
    '密码强度过低，建议修改' as security_warning
FROM users 
WHERE LENGTH(password) < 60  -- 假设使用bcrypt，长度应该是60
   OR password LIKE '%123456%'
   OR password LIKE '%password%'
LIMIT 10;

-- 检查长期未登录用户
SELECT 
    id,
    phone,
    nickname,
    last_login_time,
    DATEDIFF(NOW(), last_login_time) as days_inactive,
    '建议提醒用户更新密码' as security_suggestion
FROM users 
WHERE last_login_time < DATE_SUB(NOW(), INTERVAL 90 DAY)
  AND deleted = 0
ORDER BY last_login_time ASC
LIMIT 20;

-- ================================
-- 10. 数据备份和恢复建议
-- ================================

/*
备份策略建议：

1. 全量备份（每日）：
mysqldump --single-transaction --routines --triggers \
  --user=backup_user --password=backup_pass \
  campus_secondhand > campus_secondhand_full_$(date +%Y%m%d).sql

2. 增量备份（每小时）：
mysqlbinlog --start-datetime="2024-12-19 00:00:00" \
  --stop-datetime="2024-12-19 01:00:00" \
  /var/log/mysql/mysql-bin.000001 > incremental_backup.sql

3. 敏感数据备份加密：
mysqldump --single-transaction campus_secondhand | \
  openssl enc -aes-256-cbc -salt -out campus_backup_encrypted.sql.enc

4. 恢复测试（每月）：
定期在测试环境验证备份文件的完整性和可恢复性
*/

-- 安全加固完成提示
SELECT 
    '🔒 安全加固脚本执行完成' as message,
    '✅ 加密函数已创建' as encryption_status,
    '✅ 审计表已就绪' as audit_status,
    '✅ 安全检查程序已部署' as security_check_status,
    '🛡️ 建议定期执行安全检查' as recommendation;

/*
安全使用建议：
1. 定期执行 CALL CheckSuspiciousLogin(); 检查异常登录
2. 每小时执行 CALL AutoBlockSuspiciousIP(); 自动封禁可疑IP
3. 监控 security_audit_logs 表，关注高风险操作
4. 定期更新加密密钥
5. 实施数据库访问权限最小化原则
6. 启用MySQL的审计插件记录所有数据库操作
7. 配置SSL连接确保数据传输安全
*/