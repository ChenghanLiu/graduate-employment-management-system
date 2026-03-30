package com.example.employment.modules.auth.service;

import com.example.employment.modules.auth.dto.AuthLoginRequest;
import com.example.employment.modules.auth.dto.EnterpriseRegisterRequest;
import com.example.employment.modules.auth.dto.StudentRegisterRequest;
import com.example.employment.modules.auth.vo.AuthUserVO;

public interface AuthService {

    AuthUserVO login(AuthLoginRequest request);

    void registerStudent(StudentRegisterRequest request);

    void registerEnterprise(EnterpriseRegisterRequest request);

    AuthUserVO getCurrentUser(Long userId);
}
