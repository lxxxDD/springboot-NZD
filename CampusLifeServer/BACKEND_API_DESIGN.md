# CampusLifeApp 后端接口设计文档

## 一、发现的逻辑问题及修复建议

### 1. 登录页面 (`pages/login/login.vue`)
**问题**: 登录没有验证逻辑，直接跳转
```javascript
// 当前代码
function login() {
  uni.reLaunch({ url: '/pages/home/home' });
}
```
**建议**: 添加表单验证和登录状态管理

### 2. 市场商品发布 (`pages/market/create.vue`)
**问题**: seller 字段写死为 'Me'，应该使用当前用户名
```javascript
// 当前代码
seller: 'Me'
// 应改为
seller: user.name
```

### 3. 订单列表 (`pages/orders/orders.vue`)
**问题**: `allOrders` 只在初始化时获取一次，不会响应式更新
```javascript
// 当前代码
const allOrders = listOrders(); // 非响应式
// 应改为
const allOrders = computed(() => listOrders());
```

### 4. 消息页面 (`pages/messages/messages.vue`)
**问题**: 消息数据是硬编码的，没有与实际聊天记录关联

### 5. 支付页面 (`pages/payment/payment.vue`)
**问题**: `updateMarketItem` 的 id 类型不匹配（字符串 vs 数字）
```javascript
// itemId.value 是字符串，但商品 id 可能是数字
updateMarketItem({
  id: itemId.value,  // 需要类型转换
  status: 'sold'
})
```

### 6. 用户数据同步
**问题**: `mock.js` 和 `user.js` 存在两套用户数据，容易不同步

---

## 二、数据库设计 (MySQL)

### 2.1 用户表 (users)
```sql
CREATE TABLE users (
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
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_student_id (student_id),
  INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2.2 商品表 (market_items)
```sql
CREATE TABLE market_items (
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
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_seller (seller_id),
  INDEX idx_category (category),
  INDEX idx_status (status),
  INDEX idx_created (created_at DESC),
  FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='二手商品表';
```

### 2.3 订单表 (orders)
```sql
CREATE TABLE orders (
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
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_buyer (buyer_id),
  INDEX idx_seller (seller_id),
  INDEX idx_order_no (order_no),
  INDEX idx_status (status),
  FOREIGN KEY (buyer_id) REFERENCES users(id),
  FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

### 2.4 订单明细表 (order_items)
```sql
CREATE TABLE order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  item_id BIGINT COMMENT '商品ID',
  item_name VARCHAR(200) NOT NULL COMMENT '商品名称',
  item_image VARCHAR(500) COMMENT '商品图片',
  price DECIMAL(10,2) NOT NULL COMMENT '单价',
  quantity INT DEFAULT 1 COMMENT '数量',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_order (order_id),
  FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
```

### 2.5 聊天会话表 (conversations)
```sql
CREATE TABLE conversations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user1_id BIGINT NOT NULL,
  user2_id BIGINT NOT NULL,
  last_message_id BIGINT COMMENT '最后一条消息ID',
  last_message_time DATETIME COMMENT '最后消息时间',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_users (user1_id, user2_id),
  INDEX idx_user1 (user1_id),
  INDEX idx_user2 (user2_id),
  FOREIGN KEY (user1_id) REFERENCES users(id),
  FOREIGN KEY (user2_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';
```

### 2.6 聊天消息表 (messages)
```sql
CREATE TABLE messages (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  conversation_id BIGINT NOT NULL,
  sender_id BIGINT NOT NULL,
  receiver_id BIGINT NOT NULL,
  content TEXT NOT NULL COMMENT '消息内容',
  message_type ENUM('text', 'image', 'system') DEFAULT 'text',
  is_read TINYINT DEFAULT 0 COMMENT '是否已读',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_conversation (conversation_id),
  INDEX idx_sender (sender_id),
  INDEX idx_receiver (receiver_id),
  FOREIGN KEY (conversation_id) REFERENCES conversations(id),
  FOREIGN KEY (sender_id) REFERENCES users(id),
  FOREIGN KEY (receiver_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';
```

### 2.7 报修表 (repairs)
```sql
CREATE TABLE repairs (
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
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME,
  INDEX idx_user (user_id),
  INDEX idx_status (status),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';
```

### 2.8 活动表 (activities)
```sql
CREATE TABLE activities (
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
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_status (status),
  INDEX idx_start_time (start_time),
  FOREIGN KEY (organizer_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';
```

### 2.9 活动报名表 (activity_registrations)
```sql
CREATE TABLE activity_registrations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  activity_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  status ENUM('registered', 'cancelled', 'attended') DEFAULT 'registered',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_activity_user (activity_id, user_id),
  FOREIGN KEY (activity_id) REFERENCES activities(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';
```

### 2.10 收藏表 (favorites)
```sql
CREATE TABLE favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  item_id BIGINT NOT NULL COMMENT '商品ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_item (user_id, item_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (item_id) REFERENCES market_items(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';
```

### 2.11 交易记录表 (transactions)
```sql
CREATE TABLE transactions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  type ENUM('topup', 'payment', 'refund', 'income') NOT NULL COMMENT '类型',
  amount DECIMAL(10,2) NOT NULL COMMENT '金额',
  balance_after DECIMAL(10,2) NOT NULL COMMENT '交易后余额',
  order_id BIGINT COMMENT '关联订单ID',
  description VARCHAR(200) COMMENT '描述',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_type (type),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';
```

### 2.12 通知表 (notifications)
```sql
CREATE TABLE notifications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(100) NOT NULL,
  content TEXT NOT NULL,
  type VARCHAR(50) COMMENT '通知类型',
  is_read TINYINT DEFAULT 0,
  related_id BIGINT COMMENT '关联ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_user (user_id),
  INDEX idx_read (is_read),
  FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
```

---

## 三、后端 API 接口设计

### 3.1 用户模块 `/api/user`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| POST | /register | 用户注册 | `{studentId, username, password, email}` | `{code, data: {userId, token}}` |
| POST | /login | 用户登录 | `{studentId, password}` | `{code, data: {userInfo, token}}` |
| GET | /profile | 获取用户信息 | Header: Authorization | `{code, data: {user}}` |
| PUT | /profile | 更新用户信息 | `{username, avatar, phone}` | `{code, message}` |
| GET | /balance | 获取余额 | - | `{code, data: {balance}}` |
| POST | /topup | 充值 | `{amount, paymentMethod}` | `{code, data: {newBalance}}` |

### 3.2 商品模块 `/api/market`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | /items | 商品列表 | `?page=1&size=10&category=&keyword=&sort=` | `{code, data: {list, total}}` |
| GET | /items/:id | 商品详情 | - | `{code, data: {item}}` |
| POST | /items | 发布商品 | `{title, price, category, condition, images, description}` | `{code, data: {itemId}}` |
| PUT | /items/:id | 更新商品 | `{title, price, ...}` | `{code, message}` |
| PUT | /items/:id/status | 更新状态 | `{status: 'active'/'inactive'/'sold'}` | `{code, message}` |
| DELETE | /items/:id | 删除商品 | - | `{code, message}` |
| GET | /my-items | 我的商品 | `?status=` | `{code, data: {list}}` |
| POST | /items/:id/favorite | 收藏/取消 | - | `{code, data: {isFavorite}}` |

### 3.3 订单模块 `/api/orders`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | / | 订单列表 | `?page=1&size=10&type=&status=` | `{code, data: {list, total}}` |
| GET | /:id | 订单详情 | - | `{code, data: {order}}` |
| POST | / | 创建订单 | `{itemId, type, amount}` | `{code, data: {orderId, orderNo}}` |
| POST | /:id/pay | 支付订单 | `{paymentMethod}` | `{code, data: {success}}` |
| PUT | /:id/status | 更新状态 | `{status}` | `{code, message}` |
| POST | /:id/cancel | 取消订单 | `{reason}` | `{code, message}` |

### 3.4 消息模块 `/api/messages`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | /conversations | 会话列表 | - | `{code, data: {list}}` |
| GET | /conversations/:id | 聊天记录 | `?page=1&size=20` | `{code, data: {list, hasMore}}` |
| POST | /send | 发送消息 | `{receiverId, content, type}` | `{code, data: {messageId}}` |
| PUT | /read/:conversationId | 标记已读 | - | `{code, message}` |
| DELETE | /conversations/:id | 删除会话 | - | `{code, message}` |

### 3.5 报修模块 `/api/repairs`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | / | 报修列表 | `?status=` | `{code, data: {list}}` |
| GET | /:id | 报修详情 | - | `{code, data: {repair}}` |
| POST | / | 提交报修 | `{location, issueType, description, images}` | `{code, data: {repairId}}` |
| POST | /:id/rate | 评价 | `{rating, feedback}` | `{code, message}` |

### 3.6 活动模块 `/api/activities`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | / | 活动列表 | `?status=&page=&size=` | `{code, data: {list, total}}` |
| GET | /:id | 活动详情 | - | `{code, data: {activity}}` |
| POST | /:id/register | 报名活动 | - | `{code, message}` |
| POST | /:id/cancel | 取消报名 | - | `{code, message}` |
| GET | /my | 我的活动 | - | `{code, data: {list}}` |

### 3.7 通知模块 `/api/notifications`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| GET | / | 通知列表 | `?page=&size=` | `{code, data: {list, unreadCount}}` |
| PUT | /:id/read | 标记已读 | - | `{code, message}` |
| PUT | /read-all | 全部已读 | - | `{code, message}` |

### 3.8 文件上传 `/api/upload`

| 方法 | 路径 | 描述 | 请求参数 | 响应 |
|------|------|------|----------|------|
| POST | /image | 上传图片 | FormData: file | `{code, data: {url}}` |
| POST | /images | 批量上传 | FormData: files[] | `{code, data: {urls: []}}` |

---

## 四、前端 API 封装示例

### 4.1 创建 API 基础配置

```javascript
// api/request.js
const BASE_URL = 'http://localhost:8080/api'

export function request(options) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': uni.getStorageSync('token') || ''
      },
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data)
        } else if (res.data.code === 401) {
          // Token 过期，跳转登录
          uni.reLaunch({ url: '/pages/login/login' })
          reject(res.data)
        } else {
          uni.showToast({ title: res.data.message, icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}
```

### 4.2 用户 API

```javascript
// api/user.js
import { request } from './request'

// 登录
export function login(data) {
  return request({
    url: '/user/login',
    method: 'POST',
    data
  })
}

// 注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'POST',
    data
  })
}

// 获取用户信息
export function getUserProfile() {
  return request({
    url: '/user/profile'
  })
}

// 更新用户信息
export function updateProfile(data) {
  return request({
    url: '/user/profile',
    method: 'PUT',
    data
  })
}

// 充值
export function topup(data) {
  return request({
    url: '/user/topup',
    method: 'POST',
    data
  })
}
```

### 4.3 商品 API

```javascript
// api/market.js
import { request } from './request'

// 获取商品列表
export function getMarketItems(params) {
  return request({
    url: '/market/items',
    data: params
  })
}

// 获取商品详情
export function getMarketItemDetail(id) {
  return request({
    url: `/market/items/${id}`
  })
}

// 发布商品
export function createMarketItem(data) {
  return request({
    url: '/market/items',
    method: 'POST',
    data
  })
}

// 更新商品
export function updateMarketItem(id, data) {
  return request({
    url: `/market/items/${id}`,
    method: 'PUT',
    data
  })
}

// 更新商品状态
export function updateMarketItemStatus(id, status) {
  return request({
    url: `/market/items/${id}/status`,
    method: 'PUT',
    data: { status }
  })
}

// 删除商品
export function deleteMarketItem(id) {
  return request({
    url: `/market/items/${id}`,
    method: 'DELETE'
  })
}

// 获取我的商品
export function getMyMarketItems(status) {
  return request({
    url: '/market/my-items',
    data: { status }
  })
}

// 收藏/取消收藏
export function toggleFavorite(id) {
  return request({
    url: `/market/items/${id}/favorite`,
    method: 'POST'
  })
}
```

### 4.4 订单 API

```javascript
// api/orders.js
import { request } from './request'

// 获取订单列表
export function getOrders(params) {
  return request({
    url: '/orders',
    data: params
  })
}

// 获取订单详情
export function getOrderDetail(id) {
  return request({
    url: `/orders/${id}`
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/orders',
    method: 'POST',
    data
  })
}

// 支付订单
export function payOrder(id, paymentMethod) {
  return request({
    url: `/orders/${id}/pay`,
    method: 'POST',
    data: { paymentMethod }
  })
}

// 取消订单
export function cancelOrder(id, reason) {
  return request({
    url: `/orders/${id}/cancel`,
    method: 'POST',
    data: { reason }
  })
}
```

---

## 五、WebSocket 实时消息

### 5.1 连接配置

```javascript
// utils/websocket.js
class WebSocketClient {
  constructor() {
    this.ws = null
    this.reconnectCount = 0
    this.maxReconnect = 5
    this.listeners = {}
  }

  connect(userId) {
    const url = `ws://localhost:8080/ws?userId=${userId}`
    
    this.ws = uni.connectSocket({
      url,
      success: () => console.log('WebSocket connecting...')
    })

    this.ws.onOpen(() => {
      console.log('WebSocket connected')
      this.reconnectCount = 0
    })

    this.ws.onMessage((res) => {
      const data = JSON.parse(res.data)
      this.emit(data.type, data)
    })

    this.ws.onClose(() => {
      console.log('WebSocket closed')
      this.reconnect(userId)
    })

    this.ws.onError((err) => {
      console.error('WebSocket error:', err)
    })
  }

  reconnect(userId) {
    if (this.reconnectCount < this.maxReconnect) {
      this.reconnectCount++
      setTimeout(() => this.connect(userId), 3000)
    }
  }

  send(data) {
    if (this.ws) {
      this.ws.send({ data: JSON.stringify(data) })
    }
  }

  on(event, callback) {
    if (!this.listeners[event]) {
      this.listeners[event] = []
    }
    this.listeners[event].push(callback)
  }

  emit(event, data) {
    if (this.listeners[event]) {
      this.listeners[event].forEach(cb => cb(data))
    }
  }

  close() {
    if (this.ws) {
      this.ws.close()
    }
  }
}

export default new WebSocketClient()
```

---

## 六、后端技术栈建议

### Spring Boot 项目结构
```
campus-life-backend/
├── src/main/java/com/campus/
│   ├── CampusLifeApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── WebSocketConfig.java
│   │   └── CorsConfig.java
│   ├── controller/
│   │   ├── UserController.java
│   │   ├── MarketController.java
│   │   ├── OrderController.java
│   │   ├── MessageController.java
│   │   ├── RepairController.java
│   │   └── ActivityController.java
│   ├── service/
│   │   ├── impl/
│   │   └── ...Service.java
│   ├── mapper/
│   │   └── ...Mapper.java
│   ├── entity/
│   │   └── ...Entity.java
│   ├── dto/
│   │   └── ...DTO.java
│   ├── vo/
│   │   └── ...VO.java
│   ├── websocket/
│   │   └── ChatWebSocketHandler.java
│   └── util/
│       ├── JwtUtil.java
│       └── Result.java
├── src/main/resources/
│   ├── application.yml
│   └── mapper/*.xml
└── pom.xml
```

### Maven 依赖 (pom.xml 核心部分)
```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- MyBatis Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-boot-starter</artifactId>
        <version>3.5.3</version>
    </dependency>
    
    <!-- MySQL -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
    
    <!-- WebSocket -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    
    <!-- 文件上传 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## 七、接口预留位置标记

在前端代码中，以下位置需要替换为真实 API 调用：

### 7.1 登录页面 `pages/login/login.vue`
```javascript
// TODO: 替换为真实登录 API
// import { login } from '@/api/user'
function login() {
  // const res = await login({ studentId, password })
  // uni.setStorageSync('token', res.data.token)
  uni.reLaunch({ url: '/pages/home/home' });
}
```

### 7.2 商品列表 `pages/market/market.vue`
```javascript
// TODO: 替换为真实 API
// import { getMarketItems } from '@/api/market'
// const res = await getMarketItems({ page, size, category, keyword, sort })
```

### 7.3 发布商品 `pages/market/create.vue`
```javascript
// TODO: 替换为真实 API
// import { createMarketItem } from '@/api/market'
// const res = await createMarketItem(form)
```

### 7.4 支付页面 `pages/payment/payment.vue`
```javascript
// TODO: 替换为真实 API
// import { createOrder, payOrder } from '@/api/orders'
// const order = await createOrder({ itemId, type, amount })
// await payOrder(order.id, paymentMethod)
```

### 7.5 聊天页面 `pages/messages/chat.vue`
```javascript
// TODO: 替换为 WebSocket 实时消息
// import ws from '@/utils/websocket'
// ws.on('message', (data) => { messageList.value.push(data) })
// ws.send({ type: 'chat', receiverId, content })
```

---

## 八、总结

本文档涵盖了：
1. **逻辑问题排查** - 发现并记录了 6 个主要问题
2. **数据库设计** - 12 张核心表的完整 SQL
3. **API 接口设计** - 8 个模块共 40+ 个接口
4. **前端 API 封装** - 请求工具和各模块 API
5. **WebSocket 配置** - 实时消息方案
6. **后端技术栈** - Spring Boot 项目结构和依赖
7. **接口预留位置** - 方便后续对接

后端开发时按照此文档实现接口，前端只需替换 `store/*.js` 中的本地存储逻辑为 API 调用即可。
