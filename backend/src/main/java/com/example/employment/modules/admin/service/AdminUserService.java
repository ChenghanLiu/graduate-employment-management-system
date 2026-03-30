package com.example.employment.modules.admin.service;

import com.example.employment.modules.admin.dto.AdminUserCreateRequest;
import com.example.employment.modules.admin.dto.AdminUserRoleAssignRequest;
import com.example.employment.modules.admin.dto.AdminUserUpdateRequest;
import com.example.employment.modules.admin.vo.AdminRoleVO;
import com.example.employment.modules.admin.vo.AdminUserVO;
import java.util.List;

public interface AdminUserService {

    List<AdminUserVO> listUsers();

    AdminUserVO getUser(Long userId);

    AdminUserVO createUser(AdminUserCreateRequest request);

    AdminUserVO updateUser(Long userId, AdminUserUpdateRequest request);

    void disableUser(Long userId);

    AdminUserVO assignRoles(Long userId, AdminUserRoleAssignRequest request);

    List<AdminRoleVO> listRoles();
}
