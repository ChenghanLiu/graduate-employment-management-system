package com.example.employment.modules.statistics.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobStatisticsVO {

    private final long totalJobs;

    private final long totalApplications;

    private final long hiredCount;
}
