# 栖居家具 微信小程序

家具工厂 B2B 产品展示 + 询价工具的小程序版本。

## 开发环境配置

### 1. 打开项目
打开 **微信开发者工具** → 导入项目 → 选择 `miniprogram/` 目录
AppID: 使用测试号或填入正式 AppID

### 2. 后端配置
修改 `utils/api.js` 中的 `baseUrl`:
- 开发环境：`http://localhost:8080`（需关闭微信开发者工具的域名校验）
- 生产环境：替换为正式域名

### 3. Tab 图标
`images/` 目录需要放置 tab 图标：
- `home.png` / `home-active.png`（首页）
- `inquiry.png` / `inquiry-active.png`（询价清单）
- `mine.png` / `mine-active.png`（我的）

可使用微信小程序官方图标或自行设计 48x48px PNG。

### 4. 本地开发（关闭域名校验）
微信开发者工具 → 详情 → 本地设置 → 勾选"不校验合法域名..."

## 项目结构

```
miniprogram/
├── app.js / app.json / app.wxss   # 小程序入口
├── utils/
│   ├── api.js     # API 封装（16个接口）
│   ├── auth.js    # 登录态管理
│   └── util.js    # 工具函数
├── pages/
│   ├── index/           # 首页（分类导航 + 产品推荐）
│   ├── products/        # 产品列表（分类筛选 + 搜索）
│   ├── product-detail/  # 产品详情（轮播 + 询价）
│   ├── inquiry-list/    # 询价清单（增删改）
│   ├── inquiry-create/  # 提交询价（表单）
│   ├── inquiries/       # 我的询价单（列表）
│   ├── inquiry-detail/  # 询价单详情（报价）
│   └── profile/         # 个人中心
├── components/
│   ├── product-card/    # 产品卡片组件
│   └── tabbar/          # 底部导航组件
└── style/variables.wxss # 全局样式变量
```

## 后端 API

小程序复用主项目的后端 API，无需额外开发。
后端运行在 http://localhost:8080（开发）或部署后的正式域名。
