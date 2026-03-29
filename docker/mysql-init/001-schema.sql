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

INSERT INTO sys_role (role_code, role_name, remark)
VALUES
    ('ADMIN', '系统管理员', '初始化角色'),
    ('COUNSELOR', '辅导员', '初始化角色'),
    ('STUDENT', '学生', '初始化角色')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name), remark = VALUES(remark);
