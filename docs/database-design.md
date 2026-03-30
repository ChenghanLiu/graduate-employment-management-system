# 数据库设计

## 数据库

- 名称：`employment_system`
- 类型：MySQL 8

## 设计原则

- 在现有 RBAC 表基础上逐步补充核心业务表
- 统一使用 `BIGINT` 自增主键
- 统一包含 `status`、`create_time`、`update_time`
- 保留必要业务状态字段，不额外设计复杂状态机
- 以普通索引与唯一索引保证常用查询性能
- 避免危险级联外键，由应用层控制删除与状态流转

## 本轮表清单

### 基础 RBAC

- `sys_user`
- `sys_role`
- `sys_user_role`

### 学生与简历

- `student_profile`
- `resume`
- `resume_education`
- `resume_project`
- `resume_skill`

### 企业与招聘

- `enterprise_profile`
- `job_post`
- `job_application`

### 就业管理

- `employment_record`
- `employment_attachment`

### 公告管理

- `announcement`

## 核心表设计说明

### `student_profile`

学生档案表。与 `sys_user` 一对一关联，记录学号、学院、专业、班级、学历、毕业年份、当前就业状态等信息。

### `resume`

简历主表。每个学生保留一份主简历记录，承载求职意向、自我评价、简历完成度与默认简历标识。

### `resume_education`

简历教育经历子表。按时间维度记录学校、专业、学历、起止时间与在校描述。

### `resume_project`

简历项目经历子表。记录项目名称、担任角色、项目起止时间与项目描述。

### `resume_skill`

简历技能子表。记录技能名称、熟练度与补充说明。

### `enterprise_profile`

企业档案表。与企业用户关联，记录企业名称、统一社会信用代码、联系人、行业、规模、审核状态等信息。

### `job_post`

岗位发布表。由企业发起，记录岗位名称、工作地点、学历要求、薪资说明、招聘人数、截止日期、发布状态等信息。

### `job_application`

岗位投递表。记录学生投递岗位后的投递状态、投递时间、处理时间、备注等信息。

### `employment_record`

就业记录表。记录学生最终就业去向、就业单位、岗位名称、就业类型、入职日期、审核状态等信息。

### `employment_attachment`

就业附件表。挂载到就业记录，记录三方协议、劳动合同、offer 等材料的文件元数据。

### `announcement`

公告表。用于管理端发布通知公告，支持草稿、已发布、已下线等状态。

## 状态枚举设计

### 企业审核状态 `enterprise_profile.review_status`

- `0`：待审核
- `1`：审核通过
- `2`：审核驳回

### 岗位状态 `job_post.post_status`

- `0`：待发布
- `1`：招聘中
- `2`：已下线
- `3`：已结束

### 岗位投递状态 `job_application.application_status`

- `0`：已投递
- `1`：已查看
- `2`：通过初筛
- `3`：已录用
- `4`：不合适
- `5`：学生取消

### 就业记录审核状态 `employment_record.review_status`

- `0`：待审核
- `1`：审核通过
- `2`：审核驳回

### 学生就业状态 `student_profile.employment_status`

- `0`：未就业
- `1`：求职中
- `2`：已签约待入职
- `3`：已就业
- `4`：升学
- `5`：自主创业

## 本轮落地策略

- 新增业务表以独立 SQL 文件方式叠加到现有初始化脚本之上
- 不在本轮引入删除级联、逻辑删字段、历史版本表
- 保持表结构可支持下一轮补充实体、Mapper、Service 与接口实现
