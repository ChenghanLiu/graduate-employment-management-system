package com.example.employment.modules.admin.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.admin.service.AdminDashboardService;
import com.example.employment.modules.admin.vo.AdminDashboardOverviewVO;
import com.example.employment.security.RequireAnyRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequireAnyRole(RoleCode.ADMIN)
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/overview")
    public ApiResponse<AdminDashboardOverviewVO> getOverview() {
        return ApiResponse.success(adminDashboardService.getOverview());
    }
}
