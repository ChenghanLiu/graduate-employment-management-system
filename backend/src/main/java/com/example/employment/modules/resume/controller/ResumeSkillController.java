package com.example.employment.modules.resume.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.resume.dto.ResumeSkillCreateRequest;
import com.example.employment.modules.resume.dto.ResumeSkillUpdateRequest;
import com.example.employment.modules.resume.service.ResumeSkillService;
import com.example.employment.modules.resume.vo.ResumeSkillVO;
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
@RequestMapping("/api/resumes/current/skills")
@RequireAnyRole(RoleCode.STUDENT)
public class ResumeSkillController {

    private final ResumeSkillService resumeSkillService;
    private final CurrentUserResolver currentUserResolver;

    public ResumeSkillController(ResumeSkillService resumeSkillService, CurrentUserResolver currentUserResolver) {
        this.resumeSkillService = resumeSkillService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ApiResponse<List<ResumeSkillVO>> listCurrentSkills() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(resumeSkillService.listCurrentSkills(userId));
    }

    @PostMapping
    public ApiResponse<ResumeSkillVO> createCurrentSkill(
            @Valid @RequestBody ResumeSkillCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("技能创建成功",
                resumeSkillService.createCurrentSkill(userId, createRequest));
    }

    @PutMapping("/{id}")
    public ApiResponse<ResumeSkillVO> updateCurrentSkill(
            @PathVariable @Positive(message = "技能ID不合法") Long id,
            @Valid @RequestBody ResumeSkillUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("技能更新成功",
                resumeSkillService.updateCurrentSkill(userId, id, updateRequest));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCurrentSkill(
            @PathVariable @Positive(message = "技能ID不合法") Long id) {
        Long userId = currentUserResolver.resolveStudentUserId();
        resumeSkillService.deleteCurrentSkill(userId, id);
        return ApiResponse.success("技能删除成功", null);
    }
}
