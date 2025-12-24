# CampusLife 校园生活服务平台 - 功能配置文档

## 一、项目概述

**项目名称**: CampusLife 校园生活服务平台  
**技术栈**:
- 后端: Spring Boot 3.3.5 + MyBatis-Plus 3.5.7 + MySQL 8.0
- 管理端: Vue 3 + Vite + Element Plus + Tailwind CSS
- 移动端: uni-app + Vue 3 + uview-plus

---

## 二、数据库配置

| 配置项 | 值 |
|--------|-----|
| **数据库名** | campus_life_app |
| **数据库地址** | D:/phpstudy_pro/Extensions/MySQL8.0.12/bin |
| **连接URL** | jdbc:mysql://localhost:3306/campus_life_app |
| **用户名** | root |
| **密码** | 123456 |
| **字符集** | utf8mb4_unicode_ci |

---

## 三、数据库表结构

### 3.1 用户相关

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `users` | 用户表 | id, student_id, username, password, email, phone, avatar, balance, points, status |
| `admins` | 管理员表 | id, username, password, nickname, avatar, role_id, status |
| `roles` | 角色表 | id, name, permissions, description |

### 3.2 二手市场

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `market_items` | 二手商品表 | id, seller_id, title, description, price, original_price, category, condition_level, images, cover_image, status, view_count, favorite_count |
| `market_categories` | 商品分类表 | id, name, icon, sort |
| `favorites` | 收藏表 | id, user_id, item_id |

### 3.3 订单交易

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `orders` | 订单表 | id, order_no, buyer_id, seller_id, order_type, total_amount, payment_method, status, remark |
| `order_items` | 订单明细表 | id, order_id, item_id, item_name, item_image, price, quantity |
| `transactions` | 交易记录表 | id, user_id, type, amount, balance_after, order_id, description |

### 3.4 食堂服务

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `canteens` | 食堂表 | id, name, location, open_time, close_time, status |
| `food_items` | 菜品表 | id, canteen_id, name, price, description, image, category, status |
| `food_orders` | 订餐订单表 | id, user_id, canteen_id, total_amount, status |
| `food_order_items` | 订餐明细表 | id, order_id, food_id, quantity, price |

### 3.5 报修服务

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `repairs` | 报修表 | id, repair_no, user_id, location, issue_type, description, images, status, technician_id, rating, feedback |
| `technicians` | 维修人员表 | id, name, phone, specialty, status |

### 3.6 活动管理

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `activities` | 活动表 | id, title, description, cover_image, location, start_time, end_time, max_participants, current_participants, status |
| `activity_registrations` | 活动报名表 | id, activity_id, user_id, status |

### 3.7 消息通知

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `conversations` | 会话表 | id, user1_id, user2_id, last_message_id, last_message_time |
| `messages` | 聊天消息表 | id, conversation_id, sender_id, receiver_id, content, message_type, is_read |
| `notifications` | 通知表 | id, user_id, title, content, type, is_read, related_id |

### 3.8 AI助手

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `chat_sessions` | AI会话表 | id, session_id, user_id, title |
| `chat_messages` | AI消息表 | id, session_id, role, content |
| `campus_knowledge` | 知识库表 | id, category, question, answer, keywords, priority, status |

### 3.9 系统管理

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `system_logs` | 系统日志表 | id, type, module, operation, operator, time |
| `news` | 新闻表 | id, title, content, cover_image, category, status |

---

## 四、功能模块

### 4.1 用户端功能 (移动端)

| 模块 | 功能 | API前缀 |
|------|------|---------|
| **用户认证** | 注册、登录、个人信息管理 | /api/user |
| **二手市场** | 商品浏览、发布、收藏、购买 | /api/market |
| **订单管理** | 订单创建、支付、查看、取消 | /api/order |
| **钱包充值** | 余额查询、充值、交易记录 | /api/user/topup, /api/transactions |
| **报修服务** | 提交报修、查看进度、评价 | /api/repair |
| **校园活动** | 活动列表、详情、报名 | /api/activity |
| **消息聊天** | 用户间私聊、消息通知 | /api/message |
| **AI助手** | 智能问答、校园信息查询 | /api/chat |
| **订餐服务** | 食堂菜品、下单、订单管理 | /api/food |

### 4.2 管理端功能 (Web)

| 模块 | 页面 | 权限标识 |
|------|------|----------|
| **控制台** | 数据统计仪表盘 | dashboard |
| **用户管理** | 用户列表、状态管理 | user:list |
| **服务管理** | 报修管理、维修人员、食堂/菜品管理 | service:* |
| **运营中心** | 二手审核、分类管理、活动管理、内容发布 | operation:* |
| **财务中心** | 充值记录、交易流水 | finance:* |
| **系统设置** | 管理员账号、角色权限、系统日志 | settings:* |

---

## 五、业务逻辑说明

### 5.1 订单支付流程
```
1. 买家创建订单 → 订单状态: pending
2. 买家支付订单:
   - 检查余额是否充足
   - 扣除买家余额，增加卖家余额
   - 记录双方交易流水
   - 订单状态: paid
   - 商品状态: sold
3. 卖家发货 → 订单状态: shipping
4. 买家确认收货 → 订单状态: completed
```

### 5.2 商品状态流转
```
active → inactive (下架)
active → sold (已售出)
active → deleted (删除)
inactive → active (重新上架)
```

### 5.3 报修状态流转
```
received (已接收) → assigned (已指派) → in_progress (处理中) → completed (已完成)
```

### 5.4 订单状态流转
```
pending (待支付) → paid (已支付) → shipping (配送中) → completed (已完成)
pending → cancelled (已取消)
paid → refunded (已退款)
```

---

## 六、API接口配置

### 6.1 服务端口
| 服务 | 端口 |
|------|------|
| 后端API | 8080 |
| 管理端前端 | 3000/3001 |
| 移动端 | HBuilderX预览 |

### 6.2 JWT配置
| 配置项 | 值 |
|--------|-----|
| 密钥长度 | 64字符 (HS512) |
| Token有效期 | 24小时 (86400000ms) |
| 刷新Token有效期 | 7天 (604800000ms) |

### 6.3 文件上传配置
| 配置项 | 值 |
|--------|-----|
| 上传路径 | uploads/ |
| 访问URL | http://localhost:8080/uploads/ |
| 单文件大小限制 | 10MB |
| 请求大小限制 | 50MB |

---

## 七、管理员账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 超级管理员 |

---

## 八、已修复的问题

### 8.1 代码优化
- [x] 移除 RepairServiceImpl 中的调试代码 (System.out.println)
- [x] Mapper SQL 重构为 MyBatis-Plus 方式

### 8.2 业务逻辑修复（本次检查）

| 问题 | 修复方案 | 状态 |
|------|----------|------|
| **缺少担保交易机制** | 支付后资金暂存，确认收货后再打给卖家 | ✅ 已修复 |
| **缺少退款功能** | 已支付未发货订单支持退款，自动恢复商品状态 | ✅ 已修复 |
| **商品未锁定** | 创建订单时检查是否有未支付订单占用商品 | ✅ 已修复 |
| **状态转换无验证** | 发货只能卖家操作，确认收货只能买家操作 | ✅ 已修复 |
| **缺少卖家发货功能** | 添加 shipping 状态，卖家可确认发货 | ✅ 已修复 |

### 8.3 订单状态流转（修复后）
```
pending (待支付) 
  ↓ 买家支付
paid (已支付，担保中) 
  ↓ 卖家发货
shipping (配送中) 
  ↓ 买家确认收货 → 资金打给卖家
completed (已完成)

取消/退款路径：
pending → cancelled (直接取消)
paid → refunded (退款给买家，恢复商品)
```

### 8.4 逻辑合理性
- ✅ 订单支付: 担保交易、余额检查、确认收货后打款
- ✅ 退款功能: 已支付未发货可退款、自动恢复商品状态
- ✅ 商品锁定: 防止重复下单
- ✅ 状态验证: 严格的角色和状态转换验证
- ✅ 用户认证: 密码加密(BCrypt)、JWT鉴权
- ✅ 收藏功能: 计数同步、状态切换
- ✅ 报修服务: 工单号生成、状态流转、评价功能

---

## 九、部署配置

### 9.1 开发环境启动命令

**后端服务:**
```bash
cd CampusLifeServer
mvn spring-boot:run
```

**管理端前端:**
```bash
cd CampusLifeAdminWeb2.0
npm run dev
```

**移动端:**
```bash
# 使用 HBuilderX 打开 CampusLifeApp3.0 运行
```

### 9.2 数据库初始化
```bash
# 执行初始化脚本
mysql -u root -p < CampusLifeServer/src/main/resources/sql/init.sql
```

---

## 十、Playwright 测试环境

| 配置项 | 值 |
|--------|-----|
| Python环境路径 | d:\AGXNZDBiShe\lgfbishe\playwright_env |
| 浏览器驱动 | Chromium |
| 截图保存路径 | d:\AGXNZDBiShe\lgfbishe\screenshots |

**测试脚本运行:**
```bash
.\playwright_env\Scripts\python.exe test_admin.py
```

---

*文档生成时间: 2025-12-24*
