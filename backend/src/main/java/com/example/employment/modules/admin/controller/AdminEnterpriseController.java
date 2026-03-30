package com.example.employment.modules.admin.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileReviewRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileUpdateRequest;
import com.example.employment.modules.enterprise.service.EnterpriseProfileService;
import com.example.employment.modules.enterprise.vo.EnterpriseProfileDetailVO;
import com.example.employment.security.RequireAnyRole;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/enterprises")
@RequireAnyRole(RoleCode.ADMIN)
public class AdminEnterpriseController {

    private final EnterpriseProfileService enterpriseProfileService;

    public AdminEnterpriseController(EnterpriseProfileService enterpriseProfileService) {
        this.enterpriseProfileService = enterpriseProfileService;
    }

    @GetMapping
    public ApiResponse<List<EnterpriseProfileDetailVO>> listEnterprises() {
        return ApiResponse.success(enterpriseProfileService.listAdminProfiles());
    }

    @GetMapping("/{id}")
    public ApiResponse<EnterpriseProfileDetailVO> getEnterprise(@PathVariable Long id) {
        return ApiResponse.success(enterpriseProfileService.getAdminProfile(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<EnterpriseProfileDetailVO> updateEnterprise(@PathVariable Long id,
                                                                   @Valid @RequestBody EnterpriseProfileUpdateRequest request) {
        return ApiResponse.success("企业档案更新成功", enterpriseProfileService.updateAdminProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteEnterprise(@PathVariable Long id) {
        enterpriseProfileService.disableAdminProfile(id);
        return ApiResponse.success("企业档案已禁用", null);
    }

    @PutMapping("/{id}/review")
    @RequireAnyRole({RoleCode.COUNSELOR, RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<EnterpriseProfileDetailVO> reviewEnterprise(@PathVariable Long id,
                                                                   @Valid @RequestBody EnterpriseProfileReviewRequest request) {
        return ApiResponse.success("企业档案审核完成", enterpriseProfileService.reviewProfile(id, request));
    }
}
