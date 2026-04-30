# gstack

Use the `/browse` skill from gstack for all web browsing. Never use `mcp__claude-in-chrome__*` tools.

## Available skills

| Skill | Description |
|-------|-------------|
| `/office-hours` | Reframe product ideas before writing code |
| `/plan-ceo-review` | CEO-level product review |
| `/plan-eng-review` | Architecture, data flow, edge cases, tests |
| `/plan-design-review` | Design dimension ratings (0-10) |
| `/design-consultation` | Complete design system from scratch |
| `/design-shotgun` | Rapid design iteration |
| `/design-html` | HTML design implementation |
| `/review` | Pre-landing PR review |
| `/ship` | Run tests, review, push, open PR |
| `/land-and-deploy` | Deploy after landing |
| `/canary` | Canary release |
| `/benchmark` | Performance benchmarking |
| `/browse` | Headless browser automation |
| `/connect-chrome` | Connect to Chrome instance |
| `/qa` | QA testing with fixes |
| `/qa-only` | QA testing (report only) |
| `/design-review` | Design audit + fix loop |
| `/setup-browser-cookies` | Import browser cookies |
| `/setup-deploy` | Configure deployment |
| `/setup-gbrain` | Configure gbrain |
| `/retro` | Weekly retrospective |
| `/investigate` | Root-cause investigation |
| `/document-release` | Update release docs |
| `/codex` | Codex integration |
| `/cso` | CSO review |
| `/autoplan` | Auto-planning |
| `/plan-devex-review` | Developer experience review |
| `/devex-review` | Developer experience audit |
| `/careful` | Warn before destructive commands |
| `/freeze` | Lock edits to one directory |
| `/guard` | careful + freeze combined |
| `/unfreeze` | Remove edit restrictions |
| `/gstack-upgrade` | Update gstack |
| `/learn` | Learn from user patterns |

## 项目: 栖居家具 (qiju-furniture)

Spring Boot 3.2 + Vue 3 + Element Plus 全栈家具电商平台 → 已改造为 B2B 工厂产品展示+询价工具。

- 后端: 端口 8080, JDK 21, MySQL 3307, MinIO 9000, Redis 6379
- 前端: 端口 5173, Vite proxy /api → localhost:8080
- API 统一响应格式: `Result<T>(code, msg, data)` — 前端 Axios 拦截器自动解包
- 权限: `/api/auth/**` `/api/products/**` `/api/reviews/**` 公开; `/api/admin/**` 需 ROLE_admin; `/api/inquiry-lists/**` `/api/inquiries/**` 需认证
- **微信小程序**: `miniprogram/` 目录, 微信开发者工具导入即可运行
