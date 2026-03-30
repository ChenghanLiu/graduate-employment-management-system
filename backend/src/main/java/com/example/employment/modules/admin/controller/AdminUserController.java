package com.example.employment.modules.admin.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.admin.dto.AdminUserCreateRequest;
import com.example.employment.modules.admin.dto.AdminUserRoleAssignRequest;
import com.example.employment.modules.admin.dto.AdminUserUpdateRequest;
import com.example.employment.modules.admin.service.AdminUserService;
import com.example.employment.modules.admin.vo.AdminRoleVO;
import com.example.employment.modules.admin.vo.AdminUserVO;
import com.example.employment.security.RequireAnyRole;
import jakarta.validation.Valid;
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
@RequestMapping("/api/admin")
@RequireAnyRole(RoleCode.ADMIN)
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping("/users")
    public ApiResponse<List<AdminUserVO>> listUsers() {
        return ApiResponse.success(adminUserService.listUsers());
    }

    @GetMapping("/users/{id}")
    public ApiResponse<AdminUserVO> getUser(@PathVariable Long id) {
        return ApiResponse.success(adminUserService.getUser(id));
    }

    @PostMapping("/users")
    public ApiResponse<AdminUserVO> createUser(@Valid @RequestBody AdminUserCreateRequest request) {
        return ApiResponse.success("用户创建成功", adminUserService.createUser(request));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<AdminUserVO> updateUser(@PathVariable Long id,
                                               @Valid @RequestBody AdminUserUpdateRequest request) {
        return ApiResponse.success("用户更新成功", adminUserService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        adminUserService.disableUser(id);
        return ApiResponse.success("用户已禁用", null);
    }

    @PutMapping("/users/{id}/roles")
    public ApiResponse<AdminUserVO> assignRoles(@PathVariable Long id,
                                                @Valid @RequestBody AdminUserRoleAssignRequest request) {
        return ApiResponse.success("用户角色更新成功", adminUserService.assignRoles(id, request));
    }

    @GetMapping("/roles")
    public ApiResponse<List<AdminRoleVO>> listRoles() {
        return ApiResponse.success(adminUserService.listRoles());
    }
}
