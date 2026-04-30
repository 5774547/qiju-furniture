# 栖居家具 微信小程序改造计划

## 整体架构

```
qiju-miniprogram/          ← 微信小程序项目
├── app.js                 ← 入口
├── app.json               ← 全局配置
├── app.wxss               ← 全局样式
├── sitemap.json
├── project.config.json    ← 项目配置
├── utils/
│   ├── api.js             ← API 封装（基于 wx.request）
│   ├── auth.js            ← 登录态管理（wx.login + token）
│   └── util.js            ← 工具函数
├── pages/
│   ├── index/             ← 首页（产品分类 + 推荐）
│   ├── products/          ← 产品列表（分类筛选）
│   ├── product-detail/    ← 产品详情
│   ├── inquiry-list/      ← 询价清单
│   ├── inquiry-create/    ← 提交询价（填写联系方式）
│   ├── inquiries/         ← 我的询价单
│   ├── inquiry-detail/    ← 询价单详情
│   └── profile/           ← 个人中心
├── components/
│   ├── product-card/      ← 产品卡片组件
│   └── tabbar/            ← 底部Tab（首页、询价清单、我的）
├── images/                ← 图标资源
└── style/                 ← 样式
    └── variables.wxss     ← CSS变量
```

## 后端新增 API

### 小程序登录（新增）
```
POST /api/auth/miniapp-login
Body: { code: string }  ← wx.login 返回的临时 code
Response: { token, user }
```
流程：code → 微信服务器换取 openid → 查找或创建用户 → 返回 JWT

### 已有 API（前端复用）
- 产品：/api/products (分页), /api/products/{id}, /api/products/categories
- 询价清单：GET/POST/PUT/DELETE /api/inquiry-lists
- 询价单：POST /api/inquiries, GET /api/inquiries/my, GET /api/inquiries/{id}

## 页面功能清单

| 页面 | Tab | 功能 |
|------|-----|------|
| index | ✅ 首页 | 分类导航 + 热门产品 + 工厂介绍 |
| products | ❌ | 产品列表 + 分类筛选 + 搜索 |
| product-detail | ❌ | 产品大图 + 批发价 + 加入询价清单 + 分享 |
| inquiry-list | ✅ 询价清单 | 清单列表 + 删除 + 提交询价 |
| inquiry-create | ❌ | 填写联系人/电话/公司/地址 + 提交 |
| inquiries | ✅ 我的 | 询价单列表 + 状态 |
| inquiry-detail | ❌ | 报价金额 + 有效期 + 商品明细 |
| profile | ❌ | 个人信息 + 公司信息 |

## 关键设计决策

1. **登录方式**: 微信一键登录（wx.login + 手机号授权）
2. **分享**: 每个产品页可分享到微信群/好友
3. **底部Tab**: 首页 / 询价清单 / 我的
4. **状态**: 默认使用本地开发服务器地址，发布时改为正式域名
