-- 重置管理员密码为 admin123
-- BCrypt加密后的密码
UPDATE `sys_admin` 
SET `password` = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH'
WHERE `username` = 'admin';

-- 如果admin用户不存在，则创建
INSERT INTO `sys_admin` (`username`, `password`, `nickname`, `role_id`, `status`)
SELECT 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '超级管理员', 1, 1
WHERE NOT EXISTS (SELECT 1 FROM `sys_admin` WHERE `username` = 'admin');
