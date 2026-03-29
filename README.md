# 基于 SpringBoot 的毕业生就业管理系统

本仓库为第一轮初始化版本，仅包含单体架构项目骨架，前后端分离目录、基础 RBAC 数据表、MySQL 本地开发容器以及可启动的后端健康检查接口。

## 目录说明

- `backend`：Spring Boot 后端骨架
- `frontend`：Vue 前端骨架
- `docker`：本地开发所需的 MySQL 8 容器配置
- `docs`：需求、系统设计、数据库设计文档
- `sql`：初始化 SQL
- `scripts`：预留脚本目录

## 启动步骤

### 1. 启动 MySQL

```bash
cd docker
docker compose up -d
```

默认数据库信息：

- 数据库：`employment_system`
- 端口：`3306`
- 用户：`employment_user`
- 密码：`employment_pass`
- Root 密码：`root123456`

### 2. 启动后端

```bash
cd backend
./mvnw spring-boot:run
```

默认访问：

- 健康检查：`http://localhost:8080/api/health`

说明：

- 当前仓库内置了一个仅用于离线骨架编译的 MyBatis-Plus API 占位 Jar，后续在可联网环境中可切换为官方依赖。

如需覆盖数据库连接，可通过环境变量配置：

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认访问：

- `http://localhost:5173`
