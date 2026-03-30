package com.example.employment.modules.enterprise.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileCreateRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileReviewRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileUpdateRequest;
import com.example.employment.modules.enterprise.service.EnterpriseProfileService;
import com.example.employment.modules.enterprise.vo.EnterpriseProfileDetailVO;
import com.example.employment.modules.enterprise.vo.EnterpriseReviewStatusVO;
import com.example.employment.security.RequireAnyRole;
import com.example.employment.web.CurrentUserResolver;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
@RequestMapping("/api")
public class EnterpriseProfileController {

    private final EnterpriseProfileService enterpriseProfileService;
    private final CurrentUserResolver currentUserResolver;

    public EnterpriseProfileController(EnterpriseProfileService enterpriseProfileService,
                                       CurrentUserResolver currentUserResolver) {
        this.enterpriseProfileService = enterpriseProfileService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/enterprise-profiles/current")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<EnterpriseProfileDetailVO> getCurrentProfile() {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success(enterpriseProfileService.getCurrentProfile(userId));
    }

    @PostMapping("/enterprise-profiles/current")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<EnterpriseProfileDetailVO> createProfile(
            @Valid @RequestBody EnterpriseProfileCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success("企业档案创建成功", enterpriseProfileService.createProfile(userId, createRequest));
    }

    @PutMapping("/enterprise-profiles/current")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<EnterpriseProfileDetailVO> updateProfile(
            @Valid @RequestBody EnterpriseProfileUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success("企业档案更新成功", enterpriseProfileService.updateProfile(userId, updateRequest));
    }

    @GetMapping("/enterprise-profiles/current/review-status")
    @RequireAnyRole(RoleCode.ENTERPRISE)
    public ApiResponse<EnterpriseReviewStatusVO> getCurrentReviewStatus() {
        Long userId = currentUserResolver.resolveEnterpriseUserId();
        return ApiResponse.success(enterpriseProfileService.getCurrentReviewStatus(userId));
    }

    @PutMapping("/admin/enterprise-profiles/{profileId}/review")
    @RequireAnyRole({RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<EnterpriseProfileDetailVO> reviewProfile(
            @PathVariable @Positive(message = "企业档案ID必须为正整数") Long profileId,
            @Valid @RequestBody EnterpriseProfileReviewRequest request) {
        return ApiResponse.success("企业档案审核完成", enterpriseProfileService.reviewProfile(profileId, request));
    }
}
