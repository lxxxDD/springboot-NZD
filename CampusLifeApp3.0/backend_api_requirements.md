# CampusLifeApp 3.0 后端接口需求文档 (详细版)

本文档定义了后端 RESTful API 接口，包含详细的请求/响应结构和字段类型，旨在辅助数据库设计。

> **约定**:
> - 所有接口返回 JSON 格式。
> - 成功响应状态码为 200。
> - 失败响应格式: `{ "code": 400, "message": "错误描述" }`
> - 时间格式推荐使用 ISO 8601 (e.g., `2023-11-25T12:00:00Z`) 或 时间戳。

---

## 1. 用户认证与档案 (User & Auth)

### 1.1 登录
- **POST** `/api/auth/login`
- **Request**:
  ```json
  {
    "username": "student_id_or_email", // String, 必填
    "password": "raw_password"         // String, 必填
  }
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1Ni...", // String, JWT Token
    "user": {
      "id": 1001,                     // Long/Int, 用户唯一ID
      "studentId": "A0123456X",       // String, 学号
      "name": "Alex Tan",             // String, 姓名
      "avatar": "https://...",        // String, 头像URL
      "role": "student"               // String, 角色 (student, teacher, admin)
    }
  }
  ```

### 1.2 获取用户信息 (Profile)
- **GET** `/api/user/profile`
- **Header**: `Authorization: Bearer <token>`
- **Response**:
  ```json
  {
    "id": 1001,
    "studentId": "A0123456X",
    "name": "Alex Tan",
    "email": "alex.tan@campus.edu",
    "phone": "+65 9123 4567",
    "avatar": "https://...",
    "balance": 45.50,                 // Decimal/Float, 钱包余额
    "points": 120,                    // Int, 积分
    "department": "Computer Science", // String, 院系
    "level": "Year 3"                 // String, 年级
  }
  ```

### 1.3 钱包交易记录
- **GET** `/api/user/transactions`
- **Query**: `?page=1&limit=20`
- **Response**:
  ```json
  {
    "total": 50,
    "list": [
      {
        "id": "TXN_123456",           // String, 交易流水号
        "type": "expense",            // String, 类型: expense(支出), income(收入/充值)
        "title": "第一食堂 - 海南鸡饭", // String, 交易标题
        "amount": 4.50,               // Decimal, 金额
        "date": "2023-11-24T12:30:00Z", // DateTime, 交易时间
        "status": "success"           // String, 状态: success, pending, failed
      }
    ]
  }
  ```

---

## 2. 校园服务 (Services)

### 2.1 食堂菜单 (Food Menu)
- **GET** `/api/food/menu`
- **Query**: 
  - `category`: String (可选, e.g., "亚洲风味")
  - `keyword`: String (可选, 搜索菜品名称)
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "海南鸡饭",             // String, 菜品名称
      "price": 4.50,                 // Decimal, 价格
      "img": "https://...",          // String, 图片URL
      "cat": "亚洲风味",              // String, 分类
      "description": "招牌海南鸡饭...", // String, 描述
      "isAvailable": true,           // Boolean, 是否有货
      "stock": 100                   // Int, 库存 (可选)
    }
  ]
  ```

### 2.2 食堂实时状态
- **GET** `/api/food/canteen/status`
- **Response**:
  ```json
  {
    "id": 1,
    "name": "第一食堂",
    "isOpen": true,                  // Boolean, 是否营业
    "queueLength": 12,               // Int, 当前排队人数
    "estimatedWaitTime": 5,          // Int, 预计等待分钟
    "seatsAvailable": 23,            // Int, 剩余座位
    "seatsTotal": 80                 // Int, 总座位
  }
  ```

### 2.3 提交报修 (Repair)
- **POST** `/api/repairs`
- **Request**:
  ```json
  {
    "location": "Library L3",        // String, 地点
    "issueType": "Wifi",             // String, 问题类型 (Electric, Water, Wifi, etc.)
    "description": "无法连接...",     // String, 详细描述
    "images": ["https://..."]        // Array<String>, 图片URL列表
  }
  ```
- **Response**:
  ```json
  {
    "id": "R-2023112501",            // String, 报修单号
    "status": "Received",            // String, 初始状态
    "createTime": "..."
  }
  ```

### 2.4 报修列表
- **GET** `/api/repairs`
- **Response**:
  ```json
  [
    {
      "id": "R-2023112501",
      "location": "Library L3",
      "issueType": "Wifi",
      "status": "In Progress",       // String, 状态: Received, In Progress, Completed
      "date": "2023-11-25T10:00:00Z",
      "rating": null                 // Int, 评分 (1-5), 完成后可评价
    }
  ]
  ```

---

## 3. 二手市场 (Market)

### 3.1 商品列表
- **GET** `/api/market/items`
- **Query**: 
  - `category`: String (可选, e.g., "Books")
  - `keyword`: String (可选, 搜索商品标题)
  - `page`: Int (可选, 默认1)
- **Response**:
  ```json
  {
    "total": 100,
    "list": [
      {
        "id": 101,
        "title": "微积分教材",         // String, 标题
        "price": 25.00,              // Decimal, 价格
        "image": "https://...",      // String, 主图
        "category": "Books",         // String, 分类
        "condition": "9成新",         // String, 成色
        "publishTime": "...",        // DateTime
        "seller": {
          "id": 1002,
          "name": "John Doe",
          "avatar": "https://..."
        }
      }
    ]
  }
  ```

### 3.2 商品详情
- **GET** `/api/market/items/{id}`
- **Response**:
  ```json
  {
    "id": 101,
    "title": "微积分教材",
    "price": 25.00,
    "description": "...",            // String, 详细描述
    "images": ["url1", "url2"],      // Array<String>, 多图
    "category": "Books",
    "condition": "9成新",
    "status": "active",              // String, 状态: active(在售), sold(已出), hidden(下架)
    "seller": {
      "id": 1002,
      "name": "John Doe",
      "avatar": "https://...",
      "rating": 4.8                  // Float, 卖家信用分
    }
  }
  ```

### 3.3 发布商品
- **POST** `/api/market/items`
- **Request**:
  ```json
  {
    "title": "...",
    "price": 25.00,
    "category": "Books",
    "condition": "9成新",
    "description": "...",
    "images": ["..."]
  }
  ```

---

## 4. 订单系统 (Orders)

### 4.1 创建订单
- **POST** `/api/orders`
- **Request**:
  ```json
  {
    "type": "food",                  // String, 订单类型: food, market
    "storeId": 1,                    // Int, 商家/食堂ID (可选)
    "items": [
      { "id": 1, "qty": 2, "options": "少辣" } // 商品ID, 数量, 选项
    ],
    "totalAmount": 9.00,             // Decimal, 总金额 (前端计算供校验，或仅后端计算)
    "paymentMethod": "wallet"        // String, 支付方式
  }
  ```
- **Response**:
  ```json
  {
    "orderId": "ORD-20231125-001",
    "status": "Paid",
    "pickupCode": "A-102"            // String, 取餐码 (仅食堂订单)
  }
  ```

### 4.2 订单详情
- **GET** `/api/orders/{id}`
- **Response**:
  ```json
  {
    "id": "ORD-20231125-001",
    "type": "food",
    "status": "Processing",          // String, 状态: Pending, Paid, Processing, Ready, Completed, Cancelled
    "createTime": "...",
    "pickupCode": "A-102",
    "total": 9.00,
    "items": [
      {
        "id": 1,
        "name": "海南鸡饭",
        "price": 4.50,
        "qty": 2,
        "img": "..."
      }
    ],
    "timeline": [                    // Array, 订单进度时间轴
      { "status": "Paid", "time": "12:00" },
  ```

---

## 5. 消息与通知 (Notifications & Chat)

### 5.1 通知列表
- **GET** `/api/notifications`
- **Response**:
  ```json
  [
    {
      "id": 1,
      "type": "order",               // String, 类型: order, system, promotion
      "title": "取餐提醒",
      "message": "您的订单 #1023 已制作完成",
      "time": "...",
      "read": false,                 // Boolean, 是否已读
      "link": "/pages/orders/detail?id=..." // String, 跳转链接
    }
  ]
  ```

### 5.2 聊天消息
- **GET** `/api/chat/messages`
- **Query**: `?targetUserId=1002&beforeId=...` (分页)
- **Response**:
  ```json
  [
    {
      "id": 501,
      "senderId": 1002,              // Int, 发送者ID
      "receiverId": 1001,            // Int, 接收者ID (当前用户)
      "content": "书还在吗？",         // String, 内容
      "type": "text",                // String, 类型: text, image
      "timestamp": "..."
    }
  ]
  ```

---

## 6. 数据库设计建议 (Database Schema Suggestions)

基于上述接口，建议设计以下核心数据表：

1.  **Users (用户表)**
    *   `id` (PK, BigInt), `student_id` (String, Unique), `password_hash` (String), `name` (String), `email` (String), `phone` (String), `balance` (Decimal), `points` (Int), `avatar` (String), `created_at` (DateTime)

2.  **Transactions (交易记录表)**
    *   `id` (PK, String/UUID), `user_id` (FK), `type` (Enum: expense/income), `amount` (Decimal), `title` (String), `status` (Enum), `created_at` (DateTime)

3.  **Products (商品/菜单表)**
    *   `id` (PK, BigInt), `type` (Enum: food/market), `name` (String), `price` (Decimal), `description` (Text), `image_url` (String), `category` (String), `stock` (Int), `seller_id` (FK, nullable for food), `status` (Enum), `created_at` (DateTime)

4.  **Orders (订单表)**
    *   `id` (PK, String/UUID), `user_id` (FK), `type` (Enum: food/market), `total_amount` (Decimal), `status` (Enum), `pickup_code` (String), `created_at` (DateTime), `updated_at` (DateTime)

5.  **OrderItems (订单明细表)**
    *   `id` (PK, BigInt), `order_id` (FK), `product_id` (FK), `quantity` (Int), `price_snapshot` (Decimal), `options` (String/JSON)

6.  **Repairs (报修表)**
    *   `id` (PK, String), `user_id` (FK), `location` (String), `issue_type` (String), `description` (Text), `images` (JSON/Text), `status` (Enum), `created_at` (DateTime)

7.  **Notifications (通知表)**
    *   `id` (PK, BigInt), `user_id` (FK), `title` (String), `message` (String), `type` (String), `is_read` (Boolean), `created_at` (DateTime)

8.  **Messages (聊天消息表)**
    *   `id` (PK, BigInt), `sender_id` (FK), `receiver_id` (FK), `content` (Text), `type` (String), `created_at` (DateTime)
