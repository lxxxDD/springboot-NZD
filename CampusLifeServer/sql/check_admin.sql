-- 查询admin用户信息
SELECT 
    id,
    username,
    nickname,
    role_id,
    status,
    LEFT(password, 20) as password_preview,
    create_time
FROM sys_admin 
WHERE username = 'admin';

-- 查询超级管理员角色
SELECT * FROM sys_role WHERE id = 1;
