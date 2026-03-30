package com.example.employment.modules.employmentrecord.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmploymentRecordReviewVO {

    private final Long id;

    private final Long studentId;

    private final String studentNo;

    private final String companyName;

    private final String jobTitle;

    private final Integer employmentStatus;

    private final String employmentStatusName;

    private final Integer reviewStatus;

    private final String reviewStatusName;

    private final String reviewRemark;

    private final LocalDateTime updateTime;
}
