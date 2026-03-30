package com.example.employment.modules.statistics.service;

import com.example.employment.modules.statistics.vo.EmploymentStatusStatisticsVO;
import com.example.employment.modules.statistics.vo.JobStatisticsVO;
import com.example.employment.modules.statistics.vo.StatisticsOverviewVO;
import java.util.List;

public interface StatisticsService {

    StatisticsOverviewVO getOverview();

    List<EmploymentStatusStatisticsVO> getEmploymentStatusStatistics();

    JobStatisticsVO getJobStatistics();
}
