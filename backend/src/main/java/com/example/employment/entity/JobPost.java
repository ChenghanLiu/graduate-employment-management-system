package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("job_post")
public class JobPost extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long enterpriseId;

    private String jobTitle;

    private String jobCategory;

    private String workCity;

    private String educationRequirement;

    private BigDecimal salaryMin;

    private BigDecimal salaryMax;

    private String salaryRemark;

    private Integer hiringCount;

    private String jobDescription;

    private LocalDate deadline;

    @TableField("post_status")
    private Integer postStatus;
}
