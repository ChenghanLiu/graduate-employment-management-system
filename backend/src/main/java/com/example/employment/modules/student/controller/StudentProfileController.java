package com.example.employment.modules.student.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.student.dto.StudentProfileCreateRequest;
import com.example.employment.modules.student.dto.StudentProfileUpdateRequest;
import com.example.employment.modules.student.service.StudentProfileService;
import com.example.employment.modules.student.vo.StudentProfileDetailVO;
import com.example.employment.security.RequireAnyRole;
import com.example.employment.web.CurrentUserResolver;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/student-profiles")
@RequireAnyRole(RoleCode.STUDENT)
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final CurrentUserResolver currentUserResolver;

    public StudentProfileController(StudentProfileService studentProfileService,
                                    CurrentUserResolver currentUserResolver) {
        this.studentProfileService = studentProfileService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/current")
    public ApiResponse<StudentProfileDetailVO> getCurrentProfile() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(studentProfileService.getCurrentProfile(userId));
    }

    @PostMapping("/current")
    public ApiResponse<StudentProfileDetailVO> createProfile(
            @Valid @RequestBody StudentProfileCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("学生档案创建成功", studentProfileService.createProfile(userId, createRequest));
    }

    @PutMapping("/current")
    public ApiResponse<StudentProfileDetailVO> updateProfile(
            @Valid @RequestBody StudentProfileUpdateRequest updateRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("学生档案更新成功", studentProfileService.updateProfile(userId, updateRequest));
    }
}
