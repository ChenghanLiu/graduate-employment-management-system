CREATE DATABASE IF NOT EXISTS employment_system
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

ALTER DATABASE employment_system
    CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_unicode_ci;

USE employment_system;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    username VARCHAR(64) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(64) DEFAULT NULL COMMENT '真实姓名',
    phone VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_role_role_code (role_code),
    KEY idx_sys_role_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_sys_user_role_user_role (user_id, role_id),
    KEY idx_sys_user_role_user_id (user_id),
    KEY idx_sys_user_role_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

INSERT INTO sys_role (id, role_code, role_name, remark)
VALUES
    (1, 'ADMIN', '系统管理员', '初始化角色'),
    (2, 'COUNSELOR', '辅导员', '初始化角色'),
    (3, 'EMPLOYMENT_TEACHER', '就业指导教师', '初始化角色'),
    (4, 'STUDENT', '学生', '初始化角色'),
    (5, 'ENTERPRISE', '企业', '初始化角色')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), remark = VALUES(remark), status = 1;

INSERT INTO sys_user (id, username, password, real_name, phone, email, status)
VALUES
    (1001, 'student_demo', 'Student@123456', '学生示例账号', '13800000001', 'student@example.com', 1),
    (2001, 'enterprise_demo', 'Enterprise@123456', '企业示例账号', '13800000002', 'enterprise@example.com', 1),
    (3001, 'teacher_demo', 'Teacher@123456', '就业教师示例账号', '13800000003', 'teacher@example.com', 1),
    (3002, 'counselor_demo', 'Counselor@123456', '辅导员示例账号', '13800000004', 'counselor@example.com', 1),
    (9001, 'admin', 'Admin@123456', '系统管理员', '13800000009', 'admin@example.com', 1)
ON DUPLICATE KEY UPDATE
    password = VALUES(password),
    real_name = VALUES(real_name),
    phone = VALUES(phone),
    email = VALUES(email),
    status = VALUES(status);

INSERT INTO sys_user_role (id, user_id, role_id, status)
VALUES
    (1, 1001, 4, 1),
    (2, 2001, 5, 1),
    (3, 3001, 3, 1),
    (4, 3002, 2, 1),
    (5, 9001, 1, 1)
ON DUPLICATE KEY UPDATE status = VALUES(status);
