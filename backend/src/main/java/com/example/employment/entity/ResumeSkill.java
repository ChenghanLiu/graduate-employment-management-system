package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resume_skill")
public class ResumeSkill extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long resumeId;

    private String skillName;

    private Integer skillLevel;

    private String description;

    private Integer sortOrder;
}
