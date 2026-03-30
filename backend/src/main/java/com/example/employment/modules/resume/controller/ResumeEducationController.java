package com.example.employment.modules.resume.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.resume.dto.ResumeEducationCreateRequest;
import com.example.employment.modules.resume.dto.ResumeEducationUpdateRequest;
import com.example.employment.modules.resume.service.ResumeEducationService;
import com.example.employment.modules.resume.vo.ResumeEducationVO;
import com.example.employment.security.RequireAnyRole;
import com.example.employment.web.CurrentUserResolver;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/resumes/current/educations")
@RequireAnyRole(RoleCode.STUDENT)
public class ResumeEducationController {

    private final ResumeEducationService resumeEducationService;
    private final CurrentUserResolver currentUserResolver;

    public ResumeEducationController(ResumeEducationService resumeEducationService,
                                     CurrentUserResolver currentUserResolver) {
        this.resumeEducationService = resumeEducationService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ApiResponse<List<ResumeEducationVO>> listCurrentEducations() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(resumeEducationService.listCurrentEducations(userId));
    }

    @PostMapping
    public ApiResponse<ResumeEducationVO> createCurrentEducation(
            @Valid @RequestBody ResumeEducationCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("教育经历创建成功",
                resumeEducationService.createCurrentEducation(userId, createRequest));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResumeEducationVO> updateCurrentEducation(
            @PathVariable @Positive(message = "教育经历ID不合法") Long id,
            @Valid @RequestBody ResumeEducationUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("教育经历更新成功",
                resumeEducationService.updateCurrentEducation(userId, id, updateRequest));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCurrentEducation(
            @PathVariable @Positive(message = "教育经历ID不合法") Long id) {
        Long userId = currentUserResolver.resolveStudentUserId();
        resumeEducationService.deleteCurrentEducation(userId, id);
        return ApiResponse.success("教育经历删除成功", null);
    }
}
