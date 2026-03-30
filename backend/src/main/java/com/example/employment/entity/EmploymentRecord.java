package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employment_record")
public class EmploymentRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long jobPostId;

    private Long enterpriseId;

    private Integer employmentStatus;

    private String employmentType;

    private String companyName;

    private String jobTitle;

    private String workCity;

    private LocalDate contractStartDate;

    private BigDecimal salaryAmount;

    private String reportSource;

    @TableField("review_status")
    private Integer reviewStatus;

    private String reviewRemark;

    private Long reviewerUserId;

    private LocalDateTime reviewedTime;
}
