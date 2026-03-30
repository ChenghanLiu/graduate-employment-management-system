package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("job_application")
public class JobApplication extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long jobPostId;

    private Long studentId;

    private Long resumeId;

    @TableField("application_status")
    private Integer applicationStatus;

    private LocalDateTime applyTime;

    private LocalDateTime processedTime;

    private String remark;
}
