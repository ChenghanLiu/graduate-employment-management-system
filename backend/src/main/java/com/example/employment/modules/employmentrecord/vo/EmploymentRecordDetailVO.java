package com.example.employment.modules.employmentrecord.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmploymentRecordDetailVO {

    private final Long id;

    private final Long studentId;

    private final String studentNo;

    private final Long jobPostId;

    private final Long enterpriseId;

    private final String companyName;

    private final String employmentType;

    private final Integer employmentStatus;

    private final String employmentStatusName;

    private final String jobTitle;

    private final String workCity;

    private final LocalDate contractStartDate;

    private final BigDecimal salaryAmount;

    private final String reportSource;

    private final Integer reviewStatus;

    private final String reviewStatusName;

    private final String reviewRemark;

    private final Long reviewerUserId;

    private final LocalDateTime reviewedTime;

    private final LocalDateTime createTime;

    private final LocalDateTime updateTime;
}
