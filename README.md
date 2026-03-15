# AnyView 编程实训平台

基于 Spring Boot + Vue.js + MySQL 的编程实训平台，集成 AI 智能批改功能。

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Lombok

### 前端
- Vue 3
- Vue Router
- Pinia
- Element Plus
- Axios
- Vite

## 功能特性

### 用户管理
- 支持三种角色：管理员、教师、学生
- 用户注册、登录、权限管理
- JWT Token 认证

### 班级管理
- 教师创建班级
- 添加/移除学生
- 班级信息管理

### 题库管理
- 支持多种题型：编程题、选择题、填空题
- 题目难度分级
- 公共题库和私有题库
- 内置题库功能

### 作业管理
- 教师组题发布作业
- 设置作业开始和结束时间
- 作业发布管理

### 提交管理
- 学生提交作业
- 教师批改评价
- 提交记录查询

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
- Maven 3.6+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE anyview CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `backend/src/main/resources/application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/anyview
    username: your_username
    password: your_password
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

## API 接口

### 认证接口
- POST /api/auth/login - 用户登录
- POST /api/auth/register - 用户注册
- GET /api/auth/me - 获取当前用户信息

### 班级接口
- GET /api/classes - 获取班级列表
- POST /api/classes - 创建班级
- PUT /api/classes/{id} - 更新班级
- DELETE /api/classes/{id} - 删除班级
- POST /api/classes/{classId}/students/{studentId} - 添加学生到班级
- DELETE /api/classes/{classId}/students/{studentId} - 从班级移除学生

### 题目接口
- GET /api/questions - 获取题目列表
- GET /api/questions/public - 获取公共题目
- POST /api/questions - 创建题目
- PUT /api/questions/{id} - 更新题目
- DELETE /api/questions/{id} - 删除题目

### 作业接口
- GET /api/assignments - 获取作业列表
- POST /api/assignments - 创建作业
- PUT /api/assignments/{id} - 更新作业
- DELETE /api/assignments/{id} - 删除作业
- POST /api/assignments/{id}/publish - 发布作业

### 提交接口
- GET /api/submissions - 获取提交列表
- POST /api/submissions - 创建提交
- POST /api/submissions/{id}/grade - 批改作业

### AI 接口
- POST /api/ai/analyze - 代码分析
- POST /api/ai/grade - AI 批改
- POST /api/ai/feedback - 生成反馈

## 默认账号

系统初始化后，可以通过注册功能创建账号：
- 管理员账号需要手动在数据库中创建
- 教师和学生账号可以通过注册功能创建

## AI 配置

在 `application.yml` 中配置 AI API：
```yaml
ai:
  api:
    key: your_api_key
    url: https://api.openai.com/v1/chat/completions
```

## 开发计划

- [x] 用户认证和授权
- [x] 班级管理
- [x] 题库管理
- [x] 作业管理
- [x] 提交管理
- [x] AI 智能批改
- [ ] 代码在线运行
- [ ] 实时评测系统
- [ ] 数据统计和分析
- [ ] 移动端适配

## 许可证

MIT License
