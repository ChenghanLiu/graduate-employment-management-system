package com.example.employment.security;

import com.example.employment.common.enums.RoleCode;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class RoleGuard {

    public void checkAny(RoleCode... allowedRoles) {
        CurrentIdentity currentIdentity = CurrentIdentityContext.get();
        boolean matched = Arrays.stream(allowedRoles)
                .anyMatch(role -> role == currentIdentity.role());
        if (!matched) {
            throw new AccessDeniedException("当前角色无权访问该接口");
        }
    }
}
