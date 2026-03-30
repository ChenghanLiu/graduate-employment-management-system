package com.example.employment.modules.admin.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardOverviewVO {

    private final long totalStudents;

    private final long totalEnterprises;

    private final long totalJobs;

    private final long totalApplications;

    private final long employedStudents;

    private final long pendingEmploymentReviews;
}
