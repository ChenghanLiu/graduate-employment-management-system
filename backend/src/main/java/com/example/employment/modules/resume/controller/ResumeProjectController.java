package com.example.employment.modules.resume.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.resume.dto.ResumeProjectCreateRequest;
import com.example.employment.modules.resume.dto.ResumeProjectUpdateRequest;
import com.example.employment.modules.resume.service.ResumeProjectService;
import com.example.employment.modules.resume.vo.ResumeProjectVO;
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
@RequestMapping("/api/resumes/current/projects")
@RequireAnyRole(RoleCode.STUDENT)
public class ResumeProjectController {

    private final ResumeProjectService resumeProjectService;
    private final CurrentUserResolver currentUserResolver;

    public ResumeProjectController(ResumeProjectService resumeProjectService,
                                   CurrentUserResolver currentUserResolver) {
        this.resumeProjectService = resumeProjectService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ApiResponse<List<ResumeProjectVO>> listCurrentProjects() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(resumeProjectService.listCurrentProjects(userId));
    }

    @PostMapping
    public ApiResponse<ResumeProjectVO> createCurrentProject(
            @Valid @RequestBody ResumeProjectCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("项目经历创建成功",
                resumeProjectService.createCurrentProject(userId, createRequest));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResumeProjectVO> updateCurrentProject(
            @PathVariable @Positive(message = "项目经历ID不合法") Long id,
            @Valid @RequestBody ResumeProjectUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("项目经历更新成功",
                resumeProjectService.updateCurrentProject(userId, id, updateRequest));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCurrentProject(
            @PathVariable @Positive(message = "项目经历ID不合法") Long id) {
        Long userId = currentUserResolver.resolveStudentUserId();
        resumeProjectService.deleteCurrentProject(userId, id);
        return ApiResponse.success("项目经历删除成功", null);
    }
}
