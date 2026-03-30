package com.example.employment.modules.statistics.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmploymentStatusStatisticsVO {

    private final Integer employmentStatus;

    private final String employmentStatusName;

    private final long studentCount;
}
