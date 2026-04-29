# 🏠 栖居家具 - 全栈电商平台

基于 Spring Boot 3 + Vue 3 的全栈家具电商平台，遵循阿里巴巴 Java 开发规范。

## 技术栈

| 层级 | 技术 | 版本 |
|:----|:----|:----|
| **后端** | Spring Boot / MyBatis-Plus / Spring Security | 3.2 / 3.5.5 / 6.1 |
| **前端** | Vue 3 / Vite / Element Plus / Pinia | 3.5 / 5.4 / 2.8 |
| **数据库** | MySQL 8.0 / Redis | — |
| **对象存储** | MinIO (S3 兼容) | — |
| **部署** | Docker Compose | — |

## 快速启动

### 前置条件

- Docker & Docker Compose
- JDK 17+ (本地开发)
- Node.js 20+ & pnpm (本地开发)

### 一键部署（推荐）

```bash
# 克隆项目
cd qiju-furniture

# Docker Compose 启动全部服务
docker compose up -d

# 等待 30 秒初始化，然后访问
# http://localhost
```

### 本地开发

#### 1. 启动基础设施

```bash
# MySQL
docker run -d --name qiju-mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=qiju_furniture \
  -p 3307:3306 \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci \
  --character-set-client-handshake=FALSE

# MinIO
docker run -d --name qiju-minio \
  -e MINIO_ROOT_USER=minioadmin \
  -e MINIO_ROOT_PASSWORD=minioadmin123 \
  -p 9000:9000 -p 9001:9001 \
  minio/minio server /data --console-address ":9001"

# 导入数据库
docker exec -i qiju-mysql mysql -uroot -proot qiju_furniture < backend/sql/init.sql
```

#### 2. 启动后端

```bash
cd backend
JAVA_HOME=/path/to/jdk-21 mvn spring-boot:run -Dspring-boot.run.profiles=dev
# → http://localhost:8080
```

#### 3. 启动前端

```bash
cd frontend
pnpm install
pnpm dev
# → http://localhost:5173
```

## 项目结构

```
qiju-furniture/
├── backend/                          # 后端 Spring Boot
│   ├── sql/init.sql                  # 数据库初始化 (7张表 + 16条商品)
│   ├── pom.xml
│   └── src/main/java/com/qiju/furniture/
│       ├── common/                   # 公共模块
│       │   ├── config/               #   Cors / MyBatis-Plus / Security / MinIO
│       │   ├── exception/            #   全局异常处理
│       │   ├── result/               #   统一返回体 + 分页
│       │   ├── security/             #   JWT 认证
│       │   └── service/              #   MinIO 服务
│       └── module/                   # 业务模块
│           ├── product/              #   商品 (列表/详情/分类/分页)
│           ├── review/               #   评价
│           ├── cart/                 #   购物车
│           ├── order/                #   订单
│           ├── auth/                 #   认证 (注册/登录/JWT)
│           ├── user/                 #   用户 (信息/密码修改)
│           ├── newsletter/           #   邮件订阅
│           ├── contact/              #   咨询留言
│           └── upload/               #   文件上传 (MinIO)
│
├── frontend/                         # 前端 Vue 3
│   ├── src/
│   │   ├── views/                    #   页面
│   │   │   ├── Home.vue              #     首页 (Hero/分类/商品列表/搜索)
│   │   │   ├── ProductDetail.vue     #     商品详情
│   │   │   ├── Cart.vue              #     购物车
│   │   │   ├── auth/                 #     登录/注册/个人信息
│   │   │   └── Order*.vue            #     订单
│   │   ├── components/               #   组件 (NavBar/ProductCard/HeroCarousel...)
│   │   ├── stores/                   #   Pinia 状态 (product/cart/auth)
│   │   ├── api/                      #   API 接口层
│   │   └── router/                   #   路由 + 守卫
│   └── Dockerfile + nginx.conf
│
├── docker-compose.yml                # 一键部署
└── README.md
```

## API 文档

| 模块 | 端点 | 说明 | 需登录 |
|:----|:----|:----|:-----:|
| **认证** | `POST /api/auth/register` | 注册 | |
| | `POST /api/auth/login` | 登录，返回JWT | |
| **用户** | `GET /api/users/profile` | 个人信息 | ✅ |
| | `PUT /api/users/profile` | 修改信息 | ✅ |
| | `PUT /api/users/password` | 修改密码 | ✅ |
| **商品** | `GET /api/products` | 商品列表(分页) | |
| | `GET /api/products/{id}` | 商品详情 | |
| | `GET /api/products/categories` | 分类统计 | |
| **评价** | `GET /api/reviews/product/{id}` | 商品评价 | |
| | `POST /api/reviews` | 提交评价 | |
| **购物车** | `GET /api/cart?sessionId=` | 购物车列表 | |
| | `POST /api/cart` | 加入购物车 | |
| | `PUT /api/cart/{id}` | 修改数量 | |
| | `DELETE /api/cart/{id}` | 删除商品 | |
| | `DELETE /api/cart/clear` | 清空购物车 | |
| **订单** | `POST /api/orders` | 创建订单 | ✅ |
| | `GET /api/orders/my` | 我的订单 | ✅ |
| | `GET /api/orders/{id}` | 订单详情 | ✅ |
| **文件** | `POST /api/upload/image` | 上传图片 | ✅ |
| **订阅** | `POST /api/newsletter` | 邮件订阅 | |
| **咨询** | `POST /api/contacts` | 留言咨询 | |

## 数据库表

| 表名 | 说明 | 关键字段 |
|:----|:----|:---------|
| `product` | 商品 | name, category, price, image, specs(JSON) |
| `review` | 评价 | productId, reviewerName, rating, content |
| `cart` | 购物车 | sessionId/userId, productId, quantity |
| `orders` | 订单 | userId, orderNo, totalAmount, status |
| `order_item` | 订单明细 | orderId, productId, productName, price, qty |
| `user` | 用户 | username, password(BCrypt), email, role |
| `newsletter` | 邮件订阅 | email |
| `contact` | 咨询留言 | name, email, message |

## 开发规范

- **阿里巴巴 Java 开发手册**：包结构、命名、异常处理
- **包结构**：按模块分包 (module/xxx/controller/service/mapper/entity/dto)
- **统一返回**：`Result<T>` 包装所有 API 响应
- **认证**：JWT Bearer Token，Spring Security 过滤链
- **密码**：BCrypt 加密存储
- **文件**：MinIO S3 对象存储
