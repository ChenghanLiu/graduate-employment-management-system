package com.example.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.employment.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("enterprise_profile")
public class EnterpriseProfile extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String enterpriseName;

    private String creditCode;

    private String industryName;

    private String enterpriseScale;

    private String contactName;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String introduction;

    /**
     * 企业审核状态，必须通过 EnterpriseReviewStatusEnum 读写。
     */
    private Integer reviewStatus;

    private String reviewRemark;
}
