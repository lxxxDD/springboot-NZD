<div align="center">

# 🎓 Campus Life Server

<!-- 动态打字机效果 -->
<a href="https://github.com/lxxxDD/campus-life-server">
  <img src="https://readme-typing-svg.herokuapp.com?font=Fira+Code&weight=600&size=40&duration=3000&pause=1000&color=BD93F9&center=true&vCenter=true&width=600&lines=Campus+Life+Server;Spring+Boot+Eco-System;Next+Gen+Campus+Solution" alt="Typing SVG" />
</a>

<!-- 技术栈徽章 -->
<p>
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.5-brightgreen?style=for-the-badge&logo=springboot" />
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" />
  <img src="https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql" />
  <img src="https://img.shields.io/badge/MyBatis_Plus-3.5.7-red?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Redis-Latest-DC382D?style=for-the-badge&logo=redis&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens" />
</p>

<!-- 仓库徽章 -->
<p>
  <a href="https://github.com/lxxxDD/campus-life-server">
    <img src="https://img.shields.io/github/repo-size/lxxxDD/campus-life-server?style=flat-square&logo=github" />
  </a>
  <a href="https://github.com/lxxxDD/campus-life-server/stargazers">
    <img src="https://img.shields.io/github/stars/lxxxDD/campus-life-server?style=flat-square&logo=github" />
  </a>
  <a href="https://github.com/lxxxDD/campus-life-server/commits">
    <img src="https://img.shields.io/github/last-commit/lxxxDD/campus-life-server?style=flat-square&logo=github" />
  </a>
</p>

<!-- 星际传送门 -->
<div align="center">
  <table>
    <tr>
      <td align="center" width="200">
        <a href="https://github.com/lxxxDD/campus-life-server">
          <img src="https://img.shields.io/badge/🧠_后端核心-Current-8A2BE2?style=for-the-badge&logo=springboot&logoColor=white" />
        </a>
        <br>
        <b>CampusLifeServer</b>
        <br>
        <sub>数据中枢 & 业务大脑</sub>
      </td>
      <td align="center" width="50">⚡</td>
      <td align="center" width="200">
        <a href="https://github.com/lxxxDD/campus-life-app">
          <img src="https://img.shields.io/badge/📱_移动端-前往-42b883?style=for-the-badge&logo=android&logoColor=white" />
        </a>
        <br>
        <b>CampusLifeApp</b>
        <br>
        <sub>触手可及的校园生活</sub>
      </td>
      <td align="center" width="50">⚡</td>
      <td align="center" width="200">
        <a href="https://github.com/lxxxDD/campus-life-admin">
          <img src="https://img.shields.io/badge/💻_管理后台-前往-409eff?style=for-the-badge&logo=google-chrome&logoColor=white" />
        </a>
        <br>
        <b>CampusLifeAdmin</b>
        <br>
        <sub>运筹帷幄的指挥中心</sub>
      </td>
    </tr>
  </table>
</div>

<h3>🚀 让校园生活从未如此极客</h3>

<p>
  <a href="#-系统架构">🗺️ 系统架构</a> •
  <a href="#-核心功能">⚡ 核心功能</a> •
  <a href="http://localhost:8080/doc.html">📖 接口文档</a> •
  <a href="#-快速开始">� 快速开始</a>
</p>

</div>

---

## 🗺️ 系统架构 (System Architecture)

```mermaid
graph TD
    Client("移动端 / 管理后台") -->|RESTful API| Gateway("Nginx 网关")
    
    subgraph CoreServer["Core Server (Spring Boot)"]
        Gateway --> Auth("认证授权 JWT")
        Auth --> Controller("控制层 Web")
        Controller --> UserService("用户服务")
        Controller --> MarketService("市场服务")
        Controller --> LifeService("生活服务")
        UserService --> MP("MyBatis-Plus")
        MarketService --> MP
        LifeService --> MP
    end
    
    subgraph DataStorage["Data Storage"]
        MP --> MySQL[("MySQL 主库")]
        LifeService --> Redis[("Redis 缓存")]
        MarketService --> OSS[("文件存储")]
    end
    
    subgraph External["External"]
        LifeService --> AI("AI 大模型接口")
    end

    %% Neon Cyberpunk Palette
    %% High Contrast & Glowing Effect
    
    style Client fill:#000000,stroke:#00ffff,stroke-width:2px,color:#00ffff
    style Gateway fill:#000000,stroke:#ff00ff,stroke-width:2px,color:#ff00ff
    style CoreServer fill:#000000,stroke:#00ff00,stroke-width:2px,color:#00ff00
    style DataStorage fill:#000000,stroke:#ffff00,stroke-width:2px,color:#ffff00
    style External fill:#000000,stroke:#ff9900,stroke-width:2px,color:#ff9900
    
    linkStyle default stroke:#ffffff,stroke-width:2px
```

## 🧬 核心流程 (Core Process)

<details>
<summary><b>🔐 点击查看：JWT认证鉴权时序图</b></summary>
<br>

```mermaid
sequenceDiagram
    participant U as User
    participant C as Client
    participant S as Server
    participant DB as Database
    participant R as Redis

    U->>C: 输入账号密码
    C->>S: POST /api/login
    S->>DB: 验证用户凭证
    DB-->>S: 用户信息
    S->>S: 生成JWT Token
    S->>R: 缓存用户信息 (Exp: 7d)
    S-->>C: 返回 Token
    
    Note over C, S: 后续请求携带 Token
    
    C->>S: GET /api/user/profile (Header: Authorization)
    S->>S: 解析并校验 Token
    S->>R: 获取用户缓存
    R-->>S: UserDTO
    S-->>C: 返回用户资料
```

</details>

## 💾 数据模型 (ER Diagram)

<details>
<summary><b>🗄️ 点击查看：核心业务ER关系图</b></summary>
<br>

```mermaid
erDiagram
    USER ||--o{ MARKET_ITEM : publishes
    USER ||--o{ ORDER : creates
    USER ||--o{ REPAIR : reports
    
    MARKET_ITEM ||--o{ ORDER : contains
    
    USER {
        bigint id PK
        string username
        string phone
        int role
    }
    
    MARKET_ITEM {
        bigint id PK
        string title
        decimal price
        int status
    }
    
    ORDER {
        bigint id PK
        bigint user_id FK
        bigint item_id FK
        int status
    }
```

</details>

## 📅 开发计划 (Roadmap)

```mermaid
gantt
    title Campus Life Server 开发路线图
    dateFormat  YYYY-MM-DD
    section 基础建设
    数据库设计       :done,    des1, 2024-11-01, 7d
    后端框架搭建     :done,    des2, 2024-11-08, 5d
    认证模块开发     :done,    des3, 2024-11-13, 5d
    section 业务开发
    二手市场模块     :done,    feat1, 2024-11-20, 10d
    食堂点餐模块     :done,    feat2, 2024-12-01, 10d
    校园报修模块     :active,  feat3, 2024-12-10, 7d
    section 智能化
    AI助手接入       :         ai1,   2024-12-15, 5d
    数据大屏开发     :         ai2,   2024-12-20, 5d
```

## ⚡ 核心能力 (Core Capabilities)

| 领域 | 核心功能 | 技术实现 |
| :--- | :--- | :--- |
| **🔐 安全架构** | JWT无状态认证、RBAC权限模型 | `HandlerInterceptor`, `@CheckToken` |
| **🚀 高性能** | 多级缓存架构、连接池优化 | `Redis`, `HikariCP` |
| **💬 即时通讯** | WebSocket全双工通信 | `ServerEndpoint` |
| **🤖 AI集成** | 智能对话上下文管理 | `OkHttp`, `SSE` |
| **📝 文档工程** | 自动化接口文档 | `Knife4j`, `Swagger 3` |

## 🚀 极速部署 (Quick Start)

```bash
# 1. ⬇️ 下载神器的源代码
git clone https://github.com/lxxxDD/campus-life-server.git

# 2. 🚀 进入发射基地
cd campus-life-server

# 3. 💣 装填弹药 (数据库)
# 执行 sql/init.sql 初始化数据库结构

# 4. ⚙️ 调整参数
# vim src/main/resources/application.yml

# 5. 🔥 点火发射！
mvn spring-boot:run
```

## 🤝 贡献者 (Contributors)

<a href="https://github.com/lxxxDD/campus-life-server/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=lxxxDD/campus-life-server" />
</a>

---

<div align="center">

**Code with ☕ and ❤️**

[![Star History Chart](https://api.star-history.com/svg?repos=lxxxDD/campus-life-server&type=Date)](https://star-history.com/#lxxxDD/campus-life-server&Date)

</div>

