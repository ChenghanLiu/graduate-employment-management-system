package com.example.employment.config;

import com.example.employment.security.RequestIdentityInterceptor;
import com.example.employment.security.RoleAuthorizationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RequestIdentityInterceptor requestIdentityInterceptor;
    private final RoleAuthorizationInterceptor roleAuthorizationInterceptor;

    public WebMvcConfig(RequestIdentityInterceptor requestIdentityInterceptor,
                        RoleAuthorizationInterceptor roleAuthorizationInterceptor) {
        this.requestIdentityInterceptor = requestIdentityInterceptor;
        this.roleAuthorizationInterceptor = roleAuthorizationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdentityInterceptor).addPathPatterns("/api/**").order(0);
        registry.addInterceptor(roleAuthorizationInterceptor).addPathPatterns("/api/**").order(1);
    }
}
