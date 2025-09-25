-- 修复明文密码的数据库迁移脚本
-- 将现有的明文密码转换为BCrypt加密格式

-- 注意：这个脚本需要在应用程序中执行，因为需要使用PasswordEncoder
-- 或者手动生成BCrypt密码哈希

-- 对于明文密码 '123456'，其BCrypt哈希值为：
-- $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey

-- 更新管理员账户密码（ID=1）
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey'
WHERE id = 1 AND password = '123456';

-- 更新用户账户密码（ID=2）
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyF5PjPJSiMtdNjBEIjy6VW4.Ey'
WHERE id = 2 AND password = '123456';

-- 查询所有可能的明文密码（长度小于60且不以$2a$开头）
SELECT id, phone, nickname, password 
FROM users 
WHERE LENGTH(password) < 60 AND password NOT LIKE '$2a$%' AND deleted = 0;

-- 验证修复结果
SELECT id, phone, nickname, 
       CASE 
           WHEN password LIKE '$2a$%' THEN '已加密'
           ELSE '明文密码'
       END as password_status
FROM users 
WHERE deleted = 0
ORDER BY id;