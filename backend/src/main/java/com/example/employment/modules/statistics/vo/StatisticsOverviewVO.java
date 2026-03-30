package com.example.employment.modules.statistics.vo;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatisticsOverviewVO {

    private final long totalStudents;

    private final long employedStudents;

    private final long unemployedStudents;

    private final BigDecimal employmentRate;
}
