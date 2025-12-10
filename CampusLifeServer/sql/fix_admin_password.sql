-- 修复admin密码 - 使用完整的BCrypt密码
-- 明文密码：admin123
-- BCrypt加密后的完整密码（60个字符）
UPDATE `sys_admin` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH'
WHERE `username` = 'admin';

-- 验证更新
SELECT 
    username, 
    LENGTH(password) as password_length,
    password
FROM sys_admin 
WHERE username = 'admin';
