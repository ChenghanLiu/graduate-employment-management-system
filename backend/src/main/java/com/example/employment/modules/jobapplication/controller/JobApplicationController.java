package com.example.employment.modules.jobapplication.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.jobapplication.dto.JobApplicationCreateRequest;
import com.example.employment.modules.jobapplication.dto.JobApplicationStatusUpdateRequest;
import com.example.employment.modules.jobapplication.service.JobApplicationService;
import com.example.employment.modules.jobapplication.vo.JobApplicationDetailVO;
import com.example.employment.security.RequireAnyRole;
import com.example.employment.web.CurrentUserResolver;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final CurrentUserResolver currentUserResolver;

    public JobApplicationController(JobApplicationService jobApplicationService,
                                    CurrentUserResolver currentUserResolver) {
        this.jobApplicationService = jobApplicationService;
        this.currentUserResolver = currentUserResolver;
    }

    @PostMapping
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<JobApplicationDetailVO> createApplication(
            @Valid @RequestBody JobApplicationCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("岗位投递成功", jobApplicationService.createApplication(userId, createRequest));
    }

    @GetMapping("/mine")
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<List<JobApplicationDetailVO>> listMyApplications() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(jobApplicationService.listMyApplications(userId));
    }

    @GetMapping("/received")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<List<JobApplicationDetailVO>> listReceivedApplications() {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success(jobApplicationService.listReceivedApplications(userId));
    }

    @PutMapping("/{id}/status")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<JobApplicationDetailVO> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobApplicationStatusUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success("投递状态更新成功",
                jobApplicationService.updateApplicationStatus(userId, id, updateRequest));
    }
}
