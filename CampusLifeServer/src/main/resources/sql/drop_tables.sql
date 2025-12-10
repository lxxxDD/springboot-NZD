-- 删除 CampusLifeApp 添加的表（按外键依赖顺序删除）
USE campus_life;

-- 先删除有外键依赖的表
DROP TABLE IF EXISTS activity_registrations;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS conversations;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS repairs;
DROP TABLE IF EXISTS market_items;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS users;

-- 完成！已删除12张表
