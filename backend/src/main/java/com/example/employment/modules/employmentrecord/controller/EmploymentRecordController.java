package com.example.employment.modules.employmentrecord.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordCreateRequest;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordReviewRequest;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordUpdateRequest;
import com.example.employment.modules.employmentrecord.service.EmploymentRecordService;
import com.example.employment.modules.employmentrecord.vo.EmploymentRecordDetailVO;
import com.example.employment.modules.employmentrecord.vo.EmploymentRecordReviewVO;
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
@RequestMapping("/api/employment-records")
public class EmploymentRecordController {

    private final EmploymentRecordService employmentRecordService;
    private final CurrentUserResolver currentUserResolver;

    public EmploymentRecordController(EmploymentRecordService employmentRecordService,
                                      CurrentUserResolver currentUserResolver) {
        this.employmentRecordService = employmentRecordService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/current")
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<EmploymentRecordDetailVO> getCurrentRecord() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(employmentRecordService.getCurrentRecord(userId));
    }

    @PostMapping("/current")
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<EmploymentRecordDetailVO> createCurrentRecord(
            @Valid @RequestBody EmploymentRecordCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("就业记录创建成功",
                employmentRecordService.createCurrentRecord(userId, createRequest));
    }

    @PutMapping("/current")
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<EmploymentRecordDetailVO> updateCurrentRecord(
            @Valid @RequestBody EmploymentRecordUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("就业记录更新成功",
                employmentRecordService.updateCurrentRecord(userId, updateRequest));
    }

    @GetMapping("/review")
    @RequireAnyRole({RoleCode.COUNSELOR, RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<List<EmploymentRecordReviewVO>> listReviewRecords() {
        Long reviewerUserId = currentUserResolver.resolveTeacherUserId();
        return ApiResponse.success(employmentRecordService.listReviewRecords(reviewerUserId));
    }

    @GetMapping("/{id}")
    @RequireAnyRole({RoleCode.COUNSELOR, RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<EmploymentRecordDetailVO> getRecordDetail(@PathVariable Long id) {
        Long reviewerUserId = currentUserResolver.resolveTeacherUserId();
        return ApiResponse.success(employmentRecordService.getRecordDetail(reviewerUserId, id));
    }

    @PutMapping("/{id}/review")
    @RequireAnyRole({RoleCode.COUNSELOR, RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<EmploymentRecordDetailVO> reviewRecord(
            @PathVariable Long id,
            @Valid @RequestBody EmploymentRecordReviewRequest reviewRequest) {
        Long reviewerUserId = currentUserResolver.resolveTeacherUserId();
        return ApiResponse.success("就业记录审核完成",
                employmentRecordService.reviewRecord(reviewerUserId, id, reviewRequest));
    }
}
