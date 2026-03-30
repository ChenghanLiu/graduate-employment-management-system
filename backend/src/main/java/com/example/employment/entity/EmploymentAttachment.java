package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employment_attachment")
public class EmploymentAttachment extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long employmentRecordId;

    private String fileName;

    private String fileUrl;

    private String fileType;

    private String remark;
}
