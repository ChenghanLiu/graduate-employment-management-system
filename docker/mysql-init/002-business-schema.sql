USE employment_system;
SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    student_no VARCHAR(32) NOT NULL COMMENT '学号',
    gender TINYINT DEFAULT NULL COMMENT '性别 1男 2女',
    college_name VARCHAR(128) NOT NULL COMMENT '学院名称',
    major_name VARCHAR(128) NOT NULL COMMENT '专业名称',
    class_name VARCHAR(128) DEFAULT NULL COMMENT '班级名称',
    education_level VARCHAR(64) NOT NULL COMMENT '学历层次',
    graduation_year SMALLINT NOT NULL COMMENT '毕业年份',
    native_place VARCHAR(128) DEFAULT NULL COMMENT '生源地',
    employment_status TINYINT NOT NULL DEFAULT 0 COMMENT '就业状态 0未就业 1求职中 2已签约待入职 3已就业 4升学 5自主创业',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_student_profile_user_id (user_id),
    UNIQUE KEY uk_student_profile_student_no (student_no),
    KEY idx_student_profile_graduation_year (graduation_year),
    KEY idx_student_profile_employment_status (employment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生档案表';

CREATE TABLE IF NOT EXISTS resume (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生档案ID',
    resume_name VARCHAR(128) NOT NULL COMMENT '简历名称',
    job_intention VARCHAR(255) DEFAULT NULL COMMENT '求职意向',
    expected_city VARCHAR(128) DEFAULT NULL COMMENT '期望城市',
    expected_salary VARCHAR(64) DEFAULT NULL COMMENT '期望薪资',
    self_evaluation VARCHAR(1000) DEFAULT NULL COMMENT '自我评价',
    completion_rate TINYINT NOT NULL DEFAULT 0 COMMENT '简历完成度',
    is_default TINYINT NOT NULL DEFAULT 1 COMMENT '是否默认简历 1是 0否',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_resume_student_id (student_id),
    KEY idx_resume_is_default (is_default),
    KEY idx_resume_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历主表';

CREATE TABLE IF NOT EXISTS resume_education (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    school_name VARCHAR(128) NOT NULL COMMENT '学校名称',
    major_name VARCHAR(128) NOT NULL COMMENT '专业名称',
    education_level VARCHAR(64) NOT NULL COMMENT '学历层次',
    start_date DATE DEFAULT NULL COMMENT '开始日期',
    end_date DATE DEFAULT NULL COMMENT '结束日期',
    description VARCHAR(1000) DEFAULT NULL COMMENT '经历描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_resume_education_resume_id (resume_id),
    KEY idx_resume_education_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历教育经历表';

CREATE TABLE IF NOT EXISTS resume_project (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    project_name VARCHAR(128) NOT NULL COMMENT '项目名称',
    role_name VARCHAR(128) DEFAULT NULL COMMENT '担任角色',
    start_date DATE DEFAULT NULL COMMENT '开始日期',
    end_date DATE DEFAULT NULL COMMENT '结束日期',
    description VARCHAR(1000) DEFAULT NULL COMMENT '项目描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_resume_project_resume_id (resume_id),
    KEY idx_resume_project_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历项目经历表';

CREATE TABLE IF NOT EXISTS resume_skill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    skill_name VARCHAR(128) NOT NULL COMMENT '技能名称',
    skill_level TINYINT DEFAULT NULL COMMENT '熟练度 1了解 2熟悉 3熟练 4精通',
    description VARCHAR(500) DEFAULT NULL COMMENT '技能说明',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_resume_skill_resume_id (resume_id),
    KEY idx_resume_skill_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='简历技能表';

CREATE TABLE IF NOT EXISTS enterprise_profile (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    enterprise_name VARCHAR(255) NOT NULL COMMENT '企业名称',
    credit_code VARCHAR(64) NOT NULL COMMENT '统一社会信用代码',
    industry_name VARCHAR(128) DEFAULT NULL COMMENT '所属行业',
    enterprise_scale VARCHAR(64) DEFAULT NULL COMMENT '企业规模',
    contact_name VARCHAR(64) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(32) NOT NULL COMMENT '联系电话',
    contact_email VARCHAR(128) DEFAULT NULL COMMENT '联系邮箱',
    address VARCHAR(255) DEFAULT NULL COMMENT '企业地址',
    introduction VARCHAR(1000) DEFAULT NULL COMMENT '企业简介',
    review_status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态 0待审核 1审核通过 2审核驳回',
    review_remark VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_enterprise_profile_user_id (user_id),
    UNIQUE KEY uk_enterprise_profile_credit_code (credit_code),
    KEY idx_enterprise_profile_review_status (review_status),
    KEY idx_enterprise_profile_enterprise_name (enterprise_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业档案表';

CREATE TABLE IF NOT EXISTS job_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    enterprise_id BIGINT NOT NULL COMMENT '企业档案ID',
    job_title VARCHAR(128) NOT NULL COMMENT '岗位名称',
    job_category VARCHAR(128) DEFAULT NULL COMMENT '岗位类别',
    work_city VARCHAR(128) NOT NULL COMMENT '工作城市',
    education_requirement VARCHAR(64) DEFAULT NULL COMMENT '学历要求',
    salary_min DECIMAL(10, 2) DEFAULT NULL COMMENT '最低薪资',
    salary_max DECIMAL(10, 2) DEFAULT NULL COMMENT '最高薪资',
    salary_remark VARCHAR(64) DEFAULT NULL COMMENT '薪资说明',
    hiring_count INT NOT NULL DEFAULT 1 COMMENT '招聘人数',
    job_description VARCHAR(2000) DEFAULT NULL COMMENT '岗位描述',
    deadline DATE DEFAULT NULL COMMENT '截止日期',
    post_status TINYINT NOT NULL DEFAULT 0 COMMENT '岗位状态 0待发布 1招聘中 2已下线 3已结束',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_job_post_enterprise_id (enterprise_id),
    KEY idx_job_post_post_status (post_status),
    KEY idx_job_post_work_city (work_city),
    KEY idx_job_post_deadline (deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位发布表';

CREATE TABLE IF NOT EXISTS job_application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    job_post_id BIGINT NOT NULL COMMENT '岗位ID',
    student_id BIGINT NOT NULL COMMENT '学生档案ID',
    resume_id BIGINT NOT NULL COMMENT '简历ID',
    application_status TINYINT NOT NULL DEFAULT 0 COMMENT '投递状态 0已投递 1已查看 2通过初筛 3已录用 4不合适 5学生取消',
    apply_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '投递时间',
    processed_time DATETIME DEFAULT NULL COMMENT '处理时间',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_job_application_job_student (job_post_id, student_id),
    KEY idx_job_application_student_id (student_id),
    KEY idx_job_application_resume_id (resume_id),
    KEY idx_job_application_application_status (application_status),
    KEY idx_job_application_apply_time (apply_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岗位投递表';

CREATE TABLE IF NOT EXISTS employment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    student_id BIGINT NOT NULL COMMENT '学生档案ID',
    job_post_id BIGINT DEFAULT NULL COMMENT '关联岗位ID',
    enterprise_id BIGINT DEFAULT NULL COMMENT '企业档案ID',
    employment_status TINYINT NOT NULL COMMENT '就业状态快照 0未就业 1求职中 2已签约待入职 3已就业 4升学 5自主创业',
    employment_type VARCHAR(64) NOT NULL COMMENT '就业类型',
    company_name VARCHAR(255) NOT NULL COMMENT '就业单位名称',
    job_title VARCHAR(128) NOT NULL COMMENT '岗位名称',
    work_city VARCHAR(128) DEFAULT NULL COMMENT '就业城市',
    contract_start_date DATE DEFAULT NULL COMMENT '入职日期',
    salary_amount DECIMAL(10, 2) DEFAULT NULL COMMENT '薪资金额',
    report_source VARCHAR(64) DEFAULT NULL COMMENT '填报来源',
    review_status TINYINT NOT NULL DEFAULT 0 COMMENT '审核状态 0待审核 1审核通过 2审核驳回',
    review_remark VARCHAR(255) DEFAULT NULL COMMENT '审核备注',
    reviewer_user_id BIGINT DEFAULT NULL COMMENT '审核人用户ID',
    reviewed_time DATETIME DEFAULT NULL COMMENT '审核时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_employment_record_student_id (student_id),
    KEY idx_employment_record_student_id (student_id),
    KEY idx_employment_record_job_post_id (job_post_id),
    KEY idx_employment_record_enterprise_id (enterprise_id),
    KEY idx_employment_record_review_status (review_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就业记录表';

CREATE TABLE IF NOT EXISTS employment_attachment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    employment_record_id BIGINT NOT NULL COMMENT '就业记录ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名称',
    file_url VARCHAR(500) NOT NULL COMMENT '文件地址',
    file_type VARCHAR(64) NOT NULL COMMENT '文件类型',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_employment_attachment_record_id (employment_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='就业附件表';

CREATE TABLE IF NOT EXISTS announcement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    title VARCHAR(255) NOT NULL COMMENT '公告标题',
    content TEXT COMMENT '公告内容',
    type VARCHAR(64) NOT NULL COMMENT '公告类型',
    publish_time DATETIME DEFAULT NULL COMMENT '发布时间',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态 0下线 1已发布',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_announcement_status (status),
    KEY idx_announcement_publish_time (publish_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';
