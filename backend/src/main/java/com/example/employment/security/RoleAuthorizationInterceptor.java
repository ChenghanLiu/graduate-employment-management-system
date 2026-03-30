package com.example.employment.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RoleAuthorizationInterceptor implements HandlerInterceptor {

    private final RoleGuard roleGuard;

    public RoleAuthorizationInterceptor(RoleGuard roleGuard) {
        this.roleGuard = roleGuard;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireAnyRole annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequireAnyRole.class);
        if (annotation == null) {
            annotation = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequireAnyRole.class);
        }
        if (annotation == null) {
            return true;
        }

        roleGuard.checkAny(annotation.value());
        return true;
    }
}
