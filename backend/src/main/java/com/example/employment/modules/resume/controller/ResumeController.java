package com.example.employment.modules.resume.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.resume.dto.ResumeCreateRequest;
import com.example.employment.modules.resume.dto.ResumeUpdateRequest;
import com.example.employment.modules.resume.service.ResumeService;
import com.example.employment.modules.resume.vo.ResumeDetailVO;
import com.example.employment.security.RequireAnyRole;
import com.example.employment.web.CurrentUserResolver;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/resumes")
@RequireAnyRole(RoleCode.STUDENT)
public class ResumeController {

    private final ResumeService resumeService;
    private final CurrentUserResolver currentUserResolver;

    public ResumeController(ResumeService resumeService, CurrentUserResolver currentUserResolver) {
        this.resumeService = resumeService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/current")
    public ApiResponse<ResumeDetailVO> getCurrentResume() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(resumeService.getCurrentResume(userId));
    }

    @PostMapping("/current")
    public ApiResponse<ResumeDetailVO> createCurrentResume(
            @Valid @RequestBody ResumeCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("简历创建成功", resumeService.createCurrentResume(userId, createRequest));
    }

    @PutMapping("/current")
    public ApiResponse<ResumeDetailVO> updateCurrentResume(
            @Valid @RequestBody ResumeUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("简历更新成功", resumeService.updateCurrentResume(userId, updateRequest));
    }
}
