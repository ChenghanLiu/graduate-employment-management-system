package com.example.employment.modules.statistics.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.statistics.service.StatisticsService;
import com.example.employment.modules.statistics.vo.EmploymentStatusStatisticsVO;
import com.example.employment.modules.statistics.vo.JobStatisticsVO;
import com.example.employment.modules.statistics.vo.StatisticsOverviewVO;
import com.example.employment.security.RequireAnyRole;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@RequireAnyRole({RoleCode.COUNSELOR, RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public ApiResponse<StatisticsOverviewVO> getOverview() {
        return ApiResponse.success(statisticsService.getOverview());
    }

    @GetMapping("/employment-status")
    public ApiResponse<List<EmploymentStatusStatisticsVO>> getEmploymentStatusStatistics() {
        return ApiResponse.success(statisticsService.getEmploymentStatusStatistics());
    }

    @GetMapping("/job")
    public ApiResponse<JobStatisticsVO> getJobStatistics() {
        return ApiResponse.success(statisticsService.getJobStatistics());
    }
}
