-- 直接设置明文密码 admin123
UPDATE `sys_admin` 
SET `password` = 'admin123'
WHERE `username` = 'admin';

-- 验证
SELECT username, password FROM sys_admin WHERE username = 'admin';
