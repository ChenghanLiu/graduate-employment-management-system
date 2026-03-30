package com.example.employment.modules.jobpost.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.jobpost.dto.JobPostCreateRequest;
import com.example.employment.modules.jobpost.dto.JobPostStatusUpdateRequest;
import com.example.employment.modules.jobpost.dto.JobPostUpdateRequest;
import com.example.employment.modules.jobpost.service.JobPostService;
import com.example.employment.modules.jobpost.vo.JobPostDetailVO;
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
@RequestMapping("/api/job-posts")
public class JobPostController {

    private final JobPostService jobPostService;
    private final CurrentUserResolver currentUserResolver;

    public JobPostController(JobPostService jobPostService, CurrentUserResolver currentUserResolver) {
        this.jobPostService = jobPostService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/mine")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<List<JobPostDetailVO>> listMyJobPosts() {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success(jobPostService.listMyJobPosts(userId));
    }

    @PostMapping
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<JobPostDetailVO> createJobPost(
            @Valid @RequestBody JobPostCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success("岗位创建成功", jobPostService.createJobPost(userId, createRequest));
    }

    @PutMapping("/{id}")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<JobPostDetailVO> updateJobPost(
            @PathVariable Long id,
            @Valid @RequestBody JobPostUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success("岗位更新成功", jobPostService.updateJobPost(userId, id, updateRequest));
    }

    @PutMapping("/{id}/status")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<JobPostDetailVO> updateJobPostStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobPostStatusUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success("岗位状态更新成功", jobPostService.updateJobPostStatus(userId, id, updateRequest));
    }

    @GetMapping
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<List<JobPostDetailVO>> listOpenJobPosts() {
        return ApiResponse.success(jobPostService.listOpenJobPosts());
    }

    @GetMapping("/{id}")
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<JobPostDetailVO> getOpenJobPostDetail(@PathVariable Long id) {
        return ApiResponse.success(jobPostService.getOpenJobPostDetail(id));
    }
}
