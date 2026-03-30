package com.example.employment.web;

import com.example.employment.common.enums.RoleCode;
import com.example.employment.security.AccessDeniedException;
import com.example.employment.security.CurrentIdentity;
import com.example.employment.security.CurrentIdentityContext;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserResolver {

    public Long resolveStudentUserId() {
        return requireRole(RoleCode.STUDENT);
    }

    public Long resolveEnterpriseUserId() {
        return requireRole(RoleCode.ENTERPRISE);
    }

    public Long resolveTeacherUserId() {
        CurrentIdentity identity = CurrentIdentityContext.get();
        if (identity.role() != RoleCode.COUNSELOR
                && identity.role() != RoleCode.EMPLOYMENT_TEACHER
                && identity.role() != RoleCode.ADMIN) {
            throw new AccessDeniedException("当前角色无权访问教师侧接口");
        }
        return identity.userId();
    }

    public Long resolveAdminUserId() {
        return requireRole(RoleCode.ADMIN);
    }

    private Long requireRole(RoleCode role) {
        CurrentIdentity identity = CurrentIdentityContext.get();
        if (identity.role() != role) {
            throw new AccessDeniedException("当前角色无权访问该接口");
        }
        return identity.userId();
    }
}
