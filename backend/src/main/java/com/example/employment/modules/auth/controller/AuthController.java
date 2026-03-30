package com.example.employment.modules.auth.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.BusinessException;
import com.example.employment.modules.auth.dto.AuthLoginRequest;
import com.example.employment.modules.auth.dto.EnterpriseRegisterRequest;
import com.example.employment.modules.auth.dto.StudentRegisterRequest;
import com.example.employment.modules.auth.service.AuthService;
import com.example.employment.modules.auth.vo.AuthUserVO;
import com.example.employment.security.CurrentIdentityResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthUserVO> login(@Valid @RequestBody AuthLoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/register/student")
    public ApiResponse<Void> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        authService.registerStudent(request);
        return ApiResponse.success("注册成功", null);
    }

    @PostMapping("/register/enterprise")
    public ApiResponse<Void> registerEnterprise(@Valid @RequestBody EnterpriseRegisterRequest request) {
        authService.registerEnterprise(request);
        return ApiResponse.success("注册成功", null);
    }

    @GetMapping("/me")
    public ApiResponse<AuthUserVO> me(HttpServletRequest request) {
        String userIdHeader = request.getHeader(CurrentIdentityResolver.USER_ID_HEADER);
        if (!StringUtils.hasText(userIdHeader)) {
            throw new BusinessException(4010, "当前未登录");
        }
        Long userId = Long.parseLong(userIdHeader.trim());
        return ApiResponse.success(authService.getCurrentUser(userId));
    }
}
