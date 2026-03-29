# 系统设计

## 架构

系统采用单体架构，仓库按前后端分离组织：

- `backend`：提供 REST API
- `frontend`：提供 Vue 单页应用骨架
- `docker`：提供本地 MySQL 运行环境

## 后端设计

- 技术栈：Spring Boot 3 + Maven + Java 17 + MyBatis-Plus + MySQL 8
- 包结构：`controller`、`service`、`mapper`、`entity`、`dto`、`vo`、`config`、`common`、`security`、`module`
- 当前仅开放 `/api/health` 用于基础可用性检查

## 前端设计

- 技术栈：Vue 3 + Vite + Vue Router
- 当前仅保留首页与基础布局，后续迭代再接入业务模块

## Docker 预留

- 当前仅提供 MySQL 开发容器
- 后续可在 `docker` 目录继续补充后端与前端镜像定义

