package com.example.employment.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestIdentityInterceptor implements HandlerInterceptor {

    private final CurrentIdentityResolver currentIdentityResolver;

    public RequestIdentityInterceptor(CurrentIdentityResolver currentIdentityResolver) {
        this.currentIdentityResolver = currentIdentityResolver;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        CurrentIdentityContext.set(currentIdentityResolver.resolve(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        CurrentIdentityContext.clear();
    }
}
