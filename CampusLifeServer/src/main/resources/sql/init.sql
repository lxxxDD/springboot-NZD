-- CampusLifeApp 数据库初始化脚本
-- 创建新数据库
CREATE DATABASE IF NOT EXISTS campus_life_app DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_life_app;

-- 1. 用户表
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_id VARCHAR(20) UNIQUE NOT NULL COMMENT '学号',
  username VARCHAR(50) NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
  email VARCHAR(100) UNIQUE COMMENT '邮箱',
  phone VARCHAR(20) COMMENT '手机号',
  avatar VARCHAR(500) DEFAULT '' COMMENT '头像URL',
  balance DECIMAL(10,2) DEFAULT 0.00 COMMENT '钱包余额',
  points INT DEFAULT 0 COMMENT '积分',
  status TINYINT DEFAULT 1 COMMENT '状态: 1正常 0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_student_id (student_id),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 二手商品表
CREATE TABLE IF NOT EXISTS market_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  seller_id BIGINT NOT NULL COMMENT '卖家ID',
  title VARCHAR(200) NOT NULL COMMENT '商品标题',
  description TEXT COMMENT '商品描述',
  price DECIMAL(10,2) NOT NULL COMMENT '价格',
  original_price DECIMAL(10,2) COMMENT '原价',
  category VARCHAR(50) NOT NULL COMMENT '分类',
  condition_level VARCHAR(50) COMMENT '成色',
  images JSON COMMENT '图片列表',
  cover_image VARCHAR(500) COMMENT '封面图',
  status ENUM('active', 'inactive', 'sold', 'deleted') DEFAULT 'active' COMMENT '状态',
  view_count INT DEFAULT 0 COMMENT '浏览量',
  favorite_count INT DEFAULT 0 COMMENT '收藏数',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_seller (seller_id),
  INDEX idx_category (category),
  INDEX idx_status (status),
  INDEX idx_created (create_time DESC),
  FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='二手商品表';

-- 3. 订单表
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(32) UNIQUE NOT NULL COMMENT '订单号',
  buyer_id BIGINT NOT NULL COMMENT '买家ID',
  seller_id BIGINT COMMENT '卖家ID(二手交易)',
  order_type ENUM('food', 'market') NOT NULL COMMENT '订单类型',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  payment_method VARCHAR(20) COMMENT '支付方式',
  status ENUM('pending', 'paid', 'shipping', 'completed', 'cancelled', 'refunded') DEFAULT 'pending',
  remark TEXT COMMENT '备注',
  paid_at DATETIME COMMENT '支付时间',
  completed_at DATETIME COMMENT '完成时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_buyer (buyer_id),
  INDEX idx_seller (seller_id),
  INDEX idx_order_no (order_no),
  INDEX idx_status (status),
  FOREIGN KEY (buyer_id) REFERENCES users(id),
  FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 4. 订单明细表
CREATE TABLE IF NOT EXISTS order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  item_id BIGINT COMMENT '商品ID',
  item_name VARCHAR(200) NOT NULL COMMENT '商品名称',
  item_image VARCHAR(500) COMMENT '商品图片',
  price DECIMAL(10,2) NOT NULL COMMENT '单价',
  quantity INT DEFAULT 1 COMMENT '数量',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_order (order_id),
  FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 5. 聊天会话表
CREATE TABLE IF NOT EXISTS conversations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user1_id BIGINT NOT NULL,
  user2_id BIGINT NOT NULL,
  last_message_id BIGINT COMMENT '最后一条消息ID',
  last_message_time DATETIME COMMENT '最后消息时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_users (user1_id, user2_id),
  INDEX idx_user1 (user1_id),
  INDEX idx_user2 (user2_id),
  FOREIGN KEY (user1_id) REFERENCES users(id),
  FOREIGN KEY (user2_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

-- 6. 聊天消息表
CREATE TABLE IF NOT EXISTS messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  conversation_id BIGINT NOT NULL,
  sender_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
  content TEXT NOT NULL COMMENT '消息内容',
  message_type ENUM('text', 'image', 'system') DEFAULT 'text',
  is_read TINYINT DEFAULT 0 COMMENT '是否已读',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_conversation (conversation_id),
  INDEX idx_sender (sender_id),
  INDEX idx_receiver (receiver_id),
  FOREIGN KEY (conversation_id) REFERENCES conversations(id),
  FOREIGN KEY (sender_id) REFERENCES users(id),
  FOREIGN KEY (receiver_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 7. 报修表
CREATE TABLE IF NOT EXISTS repairs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  repair_no VARCHAR(32) UNIQUE NOT NULL COMMENT '报修单号',
  user_id BIGINT NOT NULL,
  location VARCHAR(200) NOT NULL COMMENT '报修地点',
  issue_type VARCHAR(50) NOT NULL COMMENT '问题类型',
  description TEXT COMMENT '问题描述',
  images JSON COMMENT '图片列表',
  status ENUM('received', 'assigned', 'in_progress', 'completed') DEFAULT 'received',
  technician_id BIGINT COMMENT '维修人员ID',
  rating TINYINT COMMENT '评分 1-5',
  feedback TEXT COMMENT '反馈',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME,
  INDEX idx_user (user_id),
  INDEX idx_status (status),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

-- 8. 活动表
CREATE TABLE IF NOT EXISTS activities (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL COMMENT '活动标题',
  description TEXT COMMENT '活动描述',
  cover_image VARCHAR(500) COMMENT '封面图',
  location VARCHAR(200) COMMENT '活动地点',
  start_time DATETIME NOT NULL COMMENT '开始时间',
  end_time DATETIME COMMENT '结束时间',
  max_participants INT COMMENT '最大参与人数',
  current_participants INT DEFAULT 0 COMMENT '当前参与人数',
  organizer_id BIGINT COMMENT '组织者ID',
  status ENUM('upcoming', 'ongoing', 'ended', 'cancelled') DEFAULT 'upcoming',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_status (status),
  INDEX idx_start_time (start_time),
  FOREIGN KEY (organizer_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 9. 活动报名表
CREATE TABLE IF NOT EXISTS activity_registrations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status ENUM('registered', 'cancelled', 'attended') DEFAULT 'registered',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_activity_user (activity_id, user_id),
  FOREIGN KEY (activity_id) REFERENCES activities(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- 10. 收藏表
CREATE TABLE IF NOT EXISTS favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL COMMENT '商品ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_item (user_id, item_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (item_id) REFERENCES market_items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- 11. 交易记录表
CREATE TABLE IF NOT EXISTS transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type ENUM('topup', 'payment', 'refund', 'income') NOT NULL COMMENT '类型',
  amount DECIMAL(10,2) NOT NULL COMMENT '金额',
  balance_after DECIMAL(10,2) NOT NULL COMMENT '交易后余额',
  order_id BIGINT COMMENT '关联订单ID',
  description VARCHAR(200) COMMENT '描述',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_type (type),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 12. 通知表
CREATE TABLE IF NOT EXISTS notifications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  type VARCHAR(50) COMMENT '通知类型',
  is_read TINYINT DEFAULT 0,
  related_id BIGINT COMMENT '关联ID',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_read (is_read),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 插入测试用户 (密码: 123456)
INSERT INTO users (student_id, username, password, email, balance, points, status) VALUES
('2021001', '张三', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'zhangsan@campus.edu', 100.00, 50, 1),
('2021002', '李四', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'lisi@campus.edu', 200.00, 100, 1);

-- 插入测试商品
INSERT INTO market_items (seller_id, title, description, price, original_price, category, condition_level, cover_image, status, view_count, favorite_count) VALUES
(1, '二手教材《高等数学》', '大一用过的高数教材，保存完好，有少量笔记', 25.00, 68.00, '书籍教材', '9成新', 'https://example.com/book1.jpg', 'active', 120, 15),
(1, 'Apple AirPods Pro', '使用半年，功能正常，配件齐全', 800.00, 1999.00, '数码产品', '8成新', 'https://example.com/airpods.jpg', 'active', 350, 42),
(2, '宿舍小台灯', 'LED护眼台灯，三档调光', 35.00, 89.00, '生活用品', '9成新', 'https://example.com/lamp.jpg', 'active', 80, 8);

-- 插入测试活动
INSERT INTO activities (title, description, cover_image, location, start_time, end_time, max_participants, current_participants, organizer_id, status) VALUES
('校园歌手大赛', '一年一度的校园歌手大赛，展示你的歌喉！', 'https://example.com/singing.jpg', '大学生活动中心', '2024-12-20 19:00:00', '2024-12-20 22:00:00', 200, 45, 1, 'upcoming'),
('篮球友谊赛', '各学院篮球队友谊赛，欢迎观战', 'https://example.com/basketball.jpg', '体育馆', '2024-12-15 14:00:00', '2024-12-15 17:00:00', 500, 120, 2, 'upcoming');

-- 13. AI对话会话表
CREATE TABLE IF NOT EXISTS chat_sessions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(64) UNIQUE NOT NULL COMMENT '会话ID',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  title VARCHAR(100) COMMENT '会话标题',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_session (session_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话会话表';

-- 14. AI对话消息表
CREATE TABLE IF NOT EXISTS chat_messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  session_id VARCHAR(64) NOT NULL COMMENT '会话ID',
  role VARCHAR(20) NOT NULL COMMENT '角色: user/assistant',
  content TEXT NOT NULL COMMENT '消息内容',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_session (session_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI对话消息表';

-- 15. 校园知识库表
CREATE TABLE IF NOT EXISTS campus_knowledge (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category VARCHAR(50) NOT NULL COMMENT '分类: library/canteen/repair/activity/general',
  question VARCHAR(500) NOT NULL COMMENT '问题',
  answer TEXT NOT NULL COMMENT '答案',
  keywords VARCHAR(500) COMMENT '关键词(逗号分隔)',
  priority INT DEFAULT 0 COMMENT '优先级',
  status TINYINT DEFAULT 1 COMMENT '状态: 1启用 0禁用',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_category (category),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='校园知识库表';

-- 插入知识库测试数据
INSERT INTO campus_knowledge (category, question, answer, keywords, priority, status) VALUES
('library', '图书馆开放时间是什么？', '图书馆开放时间：周一至周五 8:00-22:00，周六周日 9:00-21:00。考试周会延长至23:00。', '图书馆,开放时间,营业时间', 10, 1),
('library', '如何借书？', '携带校园卡到图书馆自助借还机或服务台办理借阅，每人最多可借10本书，借期30天，可续借1次。', '借书,借阅,图书馆', 10, 1),
('canteen', '食堂营业时间？', '一食堂：早餐6:30-9:00，午餐11:00-13:00，晚餐17:00-19:00。二食堂营业至21:00。', '食堂,营业时间,吃饭', 10, 1),
('canteen', '食堂有什么好吃的？', '一食堂推荐：红烧肉、糖醋排骨；二食堂推荐：麻辣香锅、黄焖鸡；三食堂有各地特色小吃。', '食堂,推荐,好吃', 8, 1),
('repair', '如何提交报修申请？', '在App首页点击"报修服务"，选择报修类型，填写详细地址和问题描述，上传照片后提交即可。一般1-3个工作日会有维修人员联系您。', '报修,维修,申请', 10, 1),
('activity', '最近有什么校园活动？', '您可以在App首页的"校园活动"模块查看最新活动，包括讲座、比赛、社团活动等。也可以关注学校公众号获取活动信息。', '活动,校园活动,讲座', 8, 1),
('general', '如何使用二手市场？', '在App底部导航点击"二手市场"，可以浏览或发布闲置物品。发布时请上传清晰图片，填写真实价格和商品描述。', '二手,闲置,市场', 10, 1),
('general', '校园卡丢失怎么办？', '请立即在App或校园卡服务中心挂失，然后携带身份证到一卡通中心补办，补办费用20元。', '校园卡,丢失,挂失,补办', 10, 1);
