# 基于 SpringBoot 的毕业生就业管理系统

本仓库当前包含毕业生就业管理系统后端与前端工程，其中后端已覆盖学生、企业、岗位、投递、就业登记、公告、统计，以及 round 9 所需的管理端补齐接口。

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

# 如本地未启动 MySQL，可直接使用内存库联调
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

默认访问：

- 健康检查：`http://localhost:8080/api/health`
- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

说明：

- 当前仓库内置了一个仅用于离线骨架编译的 MyBatis-Plus API 占位 Jar，后续在可联网环境中可切换为官方依赖。

如需覆盖数据库连接，可通过环境变量配置：

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USERNAME`
- `DB_PASSWORD`

## 初始账号与轻量 RBAC 说明

当前项目仍沿用 round 8 的轻量请求头 RBAC，不走完整登录态。联调时通过 `X-User-Id` 或 `X-User-Role` 指定当前身份。

默认种子账号：

- `admin` / `Admin@123456`，用户 ID `9001`，角色 `ADMIN`
- `teacher_demo` / `Teacher@123456`，用户 ID `3001`，角色 `EMPLOYMENT_TEACHER`
- `counselor_demo` / `Counselor@123456`，用户 ID `3002`，角色 `COUNSELOR`
- `student_demo` / `Student@123456`，用户 ID `1001`，角色 `STUDENT`
- `enterprise_demo` / `Enterprise@123456`，用户 ID `2001`，角色 `ENTERPRISE`

示例：

```bash
curl -H "X-User-Id: 9001" http://localhost:8080/api/admin/users
curl -H "X-User-Id: 3001" http://localhost:8080/api/statistics/overview
```

说明：

- 当前密码为交付前简化策略，管理端新增用户时如未传 `password`，默认写入 `ChangeMe123!`
- `/api/admin/**` 仅管理员可访问；教师审核类接口保留 `COUNSELOR / EMPLOYMENT_TEACHER / ADMIN`

## 本地验证建议

```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

建议至少验证：

- `GET /api/health`
- `GET /api/admin/users`
- `PUT /api/admin/users/{id}/roles`
- `GET /api/admin/enterprises`
- `GET /api/admin/students`
- `POST /api/admin/announcements`
- `GET /api/admin/dashboard/overview`

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认访问：

- `http://localhost:5173`
