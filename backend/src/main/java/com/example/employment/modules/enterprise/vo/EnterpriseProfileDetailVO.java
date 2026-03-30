package com.example.employment.modules.enterprise.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EnterpriseProfileDetailVO {

    private final Long id;

    private final Long userId;

    private final String enterpriseName;

    private final String creditCode;

    private final String industryName;

    private final String enterpriseScale;

    private final String contactName;

    private final String contactPhone;

    private final String contactEmail;

    private final String address;

    private final String introduction;

    private final Integer reviewStatus;

    private final String reviewStatusName;

    private final String reviewRemark;

    private final Integer status;

    private final LocalDateTime createTime;

    private final LocalDateTime updateTime;
}
