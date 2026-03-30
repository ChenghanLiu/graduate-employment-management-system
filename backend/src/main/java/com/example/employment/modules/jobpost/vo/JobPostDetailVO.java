package com.example.employment.modules.jobpost.vo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobPostDetailVO {

    private final Long id;

    private final Long enterpriseId;

    private final String enterpriseName;

    private final String jobTitle;

    private final String jobCategory;

    private final String workCity;

    private final String educationRequirement;

    private final BigDecimal salaryMin;

    private final BigDecimal salaryMax;

    private final String salaryRemark;

    private final Integer hiringCount;

    private final String jobDescription;

    private final LocalDate deadline;

    private final Integer postStatus;

    private final String postStatusName;

    private final LocalDateTime createTime;

    private final LocalDateTime updateTime;
}
