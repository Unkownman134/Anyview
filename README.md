# AnyView 编程实训平台

基于 Spring Boot + Vue.js + MySQL 的编程实训平台，集成 AI 智能批改功能。

## 技术栈

### 后端
- Spring Boot 3.3.6
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Redis (缓存、会话管理、分布式锁、限流)
- Elasticsearch (全文搜索)
- RabbitMQ (消息队列)
- Spring AOP (切面编程)
- Lombok

### 前端
- Vue 3.5.13
- Vue Router
- Pinia
- Element Plus
- Axios
- Vite
- TypeScript 5.7

## 功能特性

### 用户管理
- 支持三种角色：管理员、教师、学生
- 用户注册、登录、权限管理
- JWT Token 认证
- Redis会话管理
- 登录状态持久化（localStorage）
- 路由守卫保护

### 班级管理
- 教师创建班级
- 添加/移除学生
- 班级信息管理
- 教师只能查看班级和学生信息

### 题库管理
- 支持多种题型：编程题、选择题、填空题
- 题目难度分级
- 公共题库和私有题库
- 内置题库功能
- Elasticsearch全文搜索
- Redis缓存热点数据

### 作业管理
- 教师组题发布作业
- 设置作业开始和结束时间
- 作业发布管理
- 学生作业列表
- Redis缓存作业数据
- 作业总分计算

### 提交管理
- 学生提交作业
- 教师批改评价
- 提交记录查询
- AI辅助批改
- 代码质量评估
- 智能代码分析
- 自动评分
- 错误诊断和反馈
- 代码语言识别
- 支持多种题型：编程题、选择题、填空题
- 填空题教师手动批改
- 选择题和编程题自动批改
- 批改分数限制为[0,该题分]
- 教师评语必填
- 一次提交支持多个题目
- 双线程提交，编程题异步处理
- 临时文件存储在项目内

### API配置管理
- 管理员配置AI API信息
- API密钥管理
- 测试连接功能
- API配置的增删改查

### Dashboard统计
- 实时统计数据展示
- 总用户数、班级数、题目数、作业数
- 按角色统计用户分布
- 按难度统计题目分布
- 按状态统计提交分布
- 最近7天提交趋势
- 角色特定功能介绍

### 数据分析模块
- 系统总体数据分析
  - 用户活跃度趋势
  - 提交量趋势
  - 提交时段分布
  - 提交星期分布
  - 错误类型分布
  - 总体统计指标（用户数、提交数、平均分、通过率）
- 题目分析
  - 题目提交统计
  - 通过率分析
  - 平均分统计
  - 错误类型分布
  - 提交趋势分析
- 班级分析（教师视角）
  - 班级基本信息
  - 学生排名（姓名、提交数、解题数、平均分）
  - 成绩分布（饼图展示）
  - 热门题目统计
  - 作业完成情况（题目数、提交数、平均分、完成率）
  - 提交趋势分析
- 用户分析（学生视角）
  - 个人提交统计
  - 解题数量统计
  - 平均分统计
  - 通过率分析
  - 提交趋势

### 系统功能
- Redis缓存热点数据
- Redis会话管理
- Redis分布式锁
- 基于Redis的限流功能
- RabbitMQ消息队列
- Elasticsearch全文搜索
- Spring AOP切面编程
- 暗黑模式支持
- 响应式设计

### AI 功能
- 智能代码分析
- AI 辅助批改
- 代码质量评估
- 错误诊断和反馈

## 项目结构

```
Anyview/
├── backend/                 # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/anyview/
│   │   │   │   ├── entity/          # 实体类
│   │   │   │   ├── repository/      # 数据访问层
│   │   │   │   ├── service/         # 业务逻辑层
│   │   │   │   ├── controller/      # 控制器层
│   │   │   │   ├── config/          # 配置类
│   │   │   │   ├── annotation/      # 自定义注解
│   │   │   │   ├── aspect/          # AOP切面
│   │   │   │   ├── interceptor/      # 拦截器
│   │   │   │   ├── util/            # 工具类
│   │   │   │   └── dto/             # 数据传输对象
│   │   │   └── resources/
│   │   │       └── application.yml  # 配置文件
│   │   └── test/
│   └── pom.xml
└── frontend/                # 前端项目
    ├── src/
    │   ├── api/            # API 接口
    │   ├── assets/         # 静态资源
    │   ├── components/     # 组件
    │   ├── router/         # 路由配置
    │   ├── store/          # 状态管理
    │   └── views/          # 页面组件
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 7.0+
- Elasticsearch 8.0+
- RabbitMQ 3.12+
- Maven 3.6+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE anyview CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `backend/src/main/resources/application.yml` 中的配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/anyview
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0
  elasticsearch:
    uris: http://localhost:9200
    username: elastic
    password: your_password
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 后端启动

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:3000 启动

## 核心技术实现

### Redis缓存系统
- **热点数据缓存**：使用AOP切面自动缓存方法返回值
- **会话管理**：基于Redis的用户会话存储和管理
- **分布式锁**：使用Redis实现分布式锁，防止并发问题
- **限流功能**：基于Redis的请求限流，防止接口滥用
- **数据持久化**：用户登录状态和会话信息持久化存储

### Elasticsearch搜索
- 全文搜索题目内容
- 高性能的搜索查询
- 支持复杂搜索条件

### RabbitMQ消息队列
- 异步任务处理
- 系统解耦
- 消息通知

### Spring AOP
- 自定义注解：@Cacheable、@RateLimiter
- 切面编程：缓存、限流、日志等横切关注点

### 前端路由守卫
- 未登录用户自动重定向到登录页
- 已登录用户访问登录页自动重定向到主页
- 基于角色的权限控制
- 登录状态自动恢复

## API 接口

### 认证接口
- POST /auth/login - 用户登录
- POST /auth/register - 用户注册
- GET /auth/me - 获取当前用户信息

### 班级接口
- GET /classes - 获取班级列表
- POST /classes - 创建班级
- PUT /classes/{id} - 更新班级
- DELETE /classes/{id} - 删除班级
- POST /classes/{classId}/students/{studentId} - 添加学生到班级
- DELETE /classes/{classId}/students/{studentId} - 从班级移除学生

### 题目接口
- GET /questions - 获取题目列表
- GET /questions/public - 获取公共题目
- POST /questions - 创建题目
- PUT /questions/{id} - 更新题目
- DELETE /questions/{id} - 删除题目
- GET /questions/search - 搜索题目（Elasticsearch）

### 作业接口
- GET /assignments - 获取作业列表
- POST /assignments - 创建作业
- PUT /assignments/{id} - 更新作业
- DELETE /assignments/{id} - 删除作业
- POST /assignments/{id}/publish - 发布作业

### 提交接口
- GET /submissions - 获取提交列表
- POST /submissions - 创建提交
- POST /submissions/{id}/grade - 批改作业

### API配置接口
- GET /api-configs - 获取API配置列表
- POST /api-configs - 创建API配置
- PUT /api-configs/{id} - 更新API配置
- DELETE /api-configs/{id} - 删除API配置
- POST /api-configs/test - 测试API连接

### 统计接口
- GET /statistics/overall - 获取总体统计数据
- GET /statistics/question/{id} - 获取题目统计数据
- GET /statistics/user/{id} - 获取用户统计数据

### 数据分析接口
- GET /analysis/system - 获取系统总体分析数据
- GET /analysis/question/{id} - 获取题目分析数据
- GET /analysis/class/{id} - 获取班级分析数据（教师视角）
- GET /analysis/user/{id} - 获取用户分析数据（学生视角）
- POST /analysis/record-login - 记录用户登录活动

### Redis测试接口
- POST /redis/set - 设置缓存
- GET /redis/get - 获取缓存
- DELETE /redis/delete - 删除缓存
- GET /redis/rate-limit - 测试限流
- GET /redis/test-cacheable - 测试缓存注解

### Session接口
- POST /session/create - 创建会话
- GET /session/get - 获取会话
- DELETE /session/delete - 删除会话
- POST /session/update - 更新会话
- GET /session/exists - 检查会话

### AI 接口
- POST /ai/analyze - 代码分析
- POST /ai/grade - AI 批改
- POST /ai/feedback - 生成反馈

## 默认账号

系统初始化后，会自动创建管理员账号：
- 管理员：admin/123456 (邮箱：1@qq.com)
- 教师和学生账号可以通过注册功能创建

## AI 配置

AI API配置通过系统管理界面进行配置：

1. 以管理员身份登录系统
2. 进入"API配置"页面
3. 点击"新建配置"按钮
4. 填写以下信息：
   - 配置名称：如"智谱AI"
   - API密钥：智谱AI的API Key（格式：{API Key ID}.{secret}）
   - API URL：https://open.bigmodel.cn/api/paas/v4/chat/completions
   - 模型名称：glm-4-flash
5. 点击"测试连接"验证配置是否正确
6. 保存配置

系统将使用配置的AI API进行：
- 学生作业的智能代码分析
- 自动评分和反馈生成
- 错误诊断和代码建议

### 智谱AI API获取方式
1. 访问 https://open.bigmodel.cn/
2. 注册并登录账号
3. 在控制台创建API Key
4. 复制完整的API Key（包含ID和secret）

## 最新更新

### v1.3.0 (2026-03-22)
- ✅ 实现数据分析模块功能
  - 系统总体数据分析（用户活跃度、提交趋势、时段分布、星期分布、错误类型）
  - 题目分析（提交统计、通过率、平均分、错误类型、提交趋势）
  - 班级分析（学生排名、成绩分布、热门题目、作业完成情况、提交趋势）
  - 用户分析（个人统计、解题数量、平均分、通过率、提交趋势）
- ✅ 修复教师视角数据分析模块显示问题
  - 修复成绩分布图表显示为空的问题（Vue模板引用错误、成绩区间逻辑bug）
  - 修复作业完成情况显示为空的问题（懒加载问题、LEFT JOIN FETCH）
  - 修复学生排名显示问题（字段名称修正）
  - 添加AssignmentPerformance类支持作业统计
- ✅ 修复Vue组件警告
  - 修复模板ref警告（ECharts实例转换为Vue ref对象）
  - 修复Element Plus组件警告（el-radio属性替换、markRaw包装图标）
  - 统一所有组件的ref命名规范
- ✅ 技术栈升级
  - Spring Boot 3.3.6
  - Vue 3.5.13
  - TypeScript 5.7
  - Swagger/OpenAPI文档支持

### v1.2.0 (2026-03-18)
- ✅ 修复批改按钮只对填空题显示
- ✅ 修复分数限制为[0,该题分]
- ✅ 修复提交逻辑，允许一次提交多个题目
- ✅ 添加参考答案显示（编程题）
- ✅ 添加教师评语显示（所有题型）
- ✅ 优化临时文件管理，存储在项目内
- ✅ 实现双线程提交，编程题异步处理
- ✅ 添加统计API接口

### v1.1.0 (2026-03-17)
- ✅ 实现AI API配置管理功能
- ✅ 添加API配置的增删改查接口
- ✅ 实现API连接测试功能
- ✅ 集成智谱GLM-4.7-Flash API
- ✅ 实现学生作业提交和AI评分功能
- ✅ 添加代码语言识别
- ✅ 添加错误诊断和反馈
- ✅ 实现Dashboard实时统计功能
- ✅ 添加角色特定功能介绍
- ✅ 修复SPA路由刷新404问题
- ✅ 优化API配置管理界面

### v1.0.0 (2026-03-15)
- ✅ 完善Redis缓存系统
- ✅ 实现用户信息Redis缓存
- ✅ 实现题库数据Redis缓存
- ✅ 实现作业数据Redis缓存
- ✅ 实现基于Redis的session管理
- ✅ 添加Redis测试接口
- ✅ 添加Session管理接口
- ✅ 实现登录状态持久化
- ✅ 完善路由守卫功能
- ✅ 修复前端样式问题
- ✅ 支持暗黑模式
- ✅ 支持响应式设计

## 开发计划

- [x] 用户认证和授权
- [x] 班级管理
- [x] 题库管理
- [x] 作业管理
- [x] 提交管理
- [x] AI 智能批改
- [x] Redis缓存系统
- [x] Elasticsearch搜索
- [x] RabbitMQ消息队列
- [x] 分布式锁和限流
- [x] 登录状态持久化
- [x] 路由守卫保护
- [x] 数据统计和分析
- [ ] 代码在线运行
- [ ] 实时评测系统
- [ ] 移动端适配

## 许可证

MIT License