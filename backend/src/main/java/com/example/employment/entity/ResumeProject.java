package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resume_project")
public class ResumeProject extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long resumeId;

    private String projectName;

    private String roleName;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private Integer sortOrder;
}
