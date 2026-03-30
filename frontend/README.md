# 前端启动说明

## 技术栈

- Vue 3
- Vite
- Element Plus
- Vue Router
- Axios

## 本地开发

```bash
cd frontend
npm install
npm run dev
```

默认访问地址：

- `http://localhost:5173`

## 后端联调

前端通过 Vite 代理将 `/api` 请求转发到 `http://localhost:8080`。

建议先启动后端本地内存库：

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## 轻量身份切换

当前系统不接入完整登录态，前端通过顶部“身份切换”下拉框集中切换演示身份。

默认演示身份：

- 学生：`1001 / STUDENT`
- 企业：`2001 / ENTERPRISE`
- 就业教师：`3001 / EMPLOYMENT_TEACHER`
- 管理员：`9001 / ADMIN`

前端会在请求层统一注入：

- `X-User-Id`
- `X-User-Role`

## 目录结构

```text
src
├── api
├── assets
├── components
├── constants
├── layouts
├── router
├── utils
└── views
    ├── admin
    ├── common
    ├── enterprise
    └── student
```
