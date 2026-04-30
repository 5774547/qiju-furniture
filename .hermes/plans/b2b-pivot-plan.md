# 栖居家具 B2B 改造实施计划

> **目标：** 把 DTC 电商平台改造为家具工厂的 B2B 产品展示 + 分享工具
>
> **核心逻辑变化：** `购物车→付款` → `询价清单→分享→工厂报价`
>
> **估算工时：** ~6 小时

---

## Task 1: 数据库改造

### 1.1 数据库 Schema 调整

**SQL 改动：**
- `product` 表新增：`wholesale_price DECIMAL(10,2)` (批发价), `unit VARCHAR(20)` (单位)
- `cart` → 重命名为 `inquiry_list` (询价清单): 字段精简为 `id, user_id, product_id, quantity INT, remark TEXT, status TINYINT(0草稿/1已分享), create_time, update_time`
- `orders` + `order_item` → 重命名为 `inquiry` + `inquiry_item`:
  - `inquiry`: `id, inquiry_no, user_id, customer_name, customer_phone, status(0待报价/1已报价/2已确认/3已关闭), quotation_amount, remark, create_time, update_time`
  - `inquiry_item`: `id, inquiry_id, product_id, product_name, product_image, unit, quantity, remark`
- 删除 `newsletter` 表
- 可选保留或删除 `contact` 表

### 1.2 新的 Product Entity 字段

```java
// Product entity 新增
private BigDecimal wholesalePrice;  // 批发价
private String unit;                // 单位：件/套/张/把
```

### 1.3 执行 SQL

```sql
ALTER TABLE product ADD COLUMN wholesale_price DECIMAL(10,2) DEFAULT 0;
ALTER TABLE product ADD COLUMN unit VARCHAR(20) DEFAULT '件';
```

---

## Task 2: 后端 — 重构 Cart → InquiryList

### 2.1 删除旧文件
- `CartController.java`
- `CartService.java` + `CartServiceImpl.java`
- `CartMapper.java`
- `Cart.java`
- `CartAddDTO.java` + `CartUpdateDTO.java`

### 2.2 新建 InquiryList 模块
- `InquiryList.java` (Entity)
- `InquiryListMapper.java`
- `InquiryListService.java` + `InquiryListServiceImpl.java`
- `InquiryListController.java` — 5个端点：列表/添加/更新/删除/清空

---

## Task 3: 后端 — 重构 Order → Inquiry

### 3.1 保留 OrderController（改造）
改为 InquiryController，端点为 `/api/inquiries`

- `POST /api/inquiries` — 创建询价单（从询价清单提交）
- `GET /api/inquiries` — 我的询价单列表
- `GET /api/inquiries/{id}` — 询价单详情
- `GET /api/inquiries/export/excel` — 导出

### 3.2 新建 Inquiry Entity
- `Inquiry.java`（简化，去掉支付/优惠券相关字段）
- `InquiryItem.java`
- `InquiryMapper.java` + `InquiryItemMapper.java`
- `InquiryService.java` + `InquiryServiceImpl.java`
- `InquiryCreateDTO.java`
- `InquiryVO.java`

### 3.3 改造 AdminOrderController → AdminInquiryController
- 查看所有询价单
- 回复报价（PUT 设置 quotation_amount + status=1）
- 关闭询价单

---

## Task 4: 后端 — 删除/简化

### 4.1 删除
- `NewsletterController.java`
- `NewsletterService.java` + `NewsletterServiceImpl.java`
- `NewsletterMapper.java`
- `Newsletter.java`
- `NewsletterSubscribeDTO.java`
- `ContactController.java` + 相关文件（可选）
- `frontend/src/api/newsletter.js`
- `frontend/src/api/contact.js`

### 4.2 SecurityConfig 更新
删掉：
```java
.requestMatchers("/api/newsletter/**").permitAll()
.requestMatchers("/api/contacts/**").permitAll()
```
增加：
```java
.requestMatchers("/api/inquiry-lists/**").permitAll()
```



---

## Task 5: 前端改造 — Store 和 API

### 5.1 重写 cart.js → inquiryList.js
```javascript
// src/api/inquiryList.js
export function getInquiryList()
export function addToInquiryList(data)
export function updateInquiryItem(id, data)
export function removeFromInquiryList(id)
export function clearInquiryList()
```

### 5.2 重写 order.js → inquiry.js
```javascript
// src/api/inquiry.js
export function createInquiry(data)
export function getInquiries()
export function getInquiryDetail(id)
```

### 5.3 重写 cart store → inquiryList store
```javascript
// src/stores/inquiryList.js
// items, count, addItem, removeItem, clear
```

---

## Task 6: 前端改造 — 组件和页面

### 6.1 ProductCard.vue
- "加入购物车" → "加入询价清单"
- 显示批发价 + 零售价
- 增加"分享到微信"按钮

### 6.2 NavBar.vue
- "购物车" → "询价清单"（图标改）
- 去掉"帮助中心"
- 增加"联系工厂"
- 用户下拉菜单："我的订单" → "我的询价单"

### 6.3 Cart.vue → InquiryList.vue
- 页面重构：询价清单样式
- 去掉优惠券输入
- 增加"提交询价"按钮 → 弹出联系方式表单
- 增加"清空清单"按钮

### 6.4 OrderList.vue → InquiryListPage.vue
- "我的订单" → "我的询价单"
- 展示询价单卡片 + 报价状态

### 6.5 OrderDetail.vue → InquiryDetail.vue
- 展示报价金额 + 有效期
- 去掉支付信息

### 6.6 Home.vue
- 首页改为工厂介绍 + 产品目录
- 去掉 DTC 促销横幅
- 增加"关于工厂"区块

### 6.7 Register.vue
- 增加公司名称、微信、所在县市字段

---

## Task 7: 前端改造 — 路由更新

```javascript
// router/index.js
'/cart' → '/inquiry-list' (询价清单)
'/order' → '/inquiries' (我的询价单)
'/order/:id' → '/inquiries/:id' (询价单详情)
'/admin/orders' → '/admin/inquiries' (管理后台询价单)
```

---

## Task 8: 测试验证

1. 编译后端打包
2. 重启后端
3. 重启前端
4. 浏览器验证每个页面正确加载
5. 验证产品列表、添加询价清单、提交询价流程
6. 验证管理员查看和回复报价

---

## 优先级

```
P0 (必须改): Task 2, 3, 4 — 后端核心业务逻辑
P1 (高优先): Task 5, 6.1, 6.2, 6.3 — 前端核心体验
P2 (中优先): Task 6.4, 6.5, 6.6, 6.7 — 前端页面改造
P3 (低优先): Task 7, 8 — 路由和收尾
```
