package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("resume")
public class Resume extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private String resumeName;

    private String jobIntention;

    private String expectedCity;

    private String expectedSalary;

    private String selfEvaluation;

    private Integer completionRate;

    private Integer isDefault;
}
