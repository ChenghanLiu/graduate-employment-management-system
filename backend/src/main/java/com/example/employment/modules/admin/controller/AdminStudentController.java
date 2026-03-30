package com.example.employment.modules.admin.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.student.dto.StudentProfileUpdateRequest;
import com.example.employment.modules.student.service.StudentProfileService;
import com.example.employment.modules.student.vo.StudentProfileDetailVO;
import com.example.employment.security.RequireAnyRole;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/students")
@RequireAnyRole(RoleCode.ADMIN)
public class AdminStudentController {

    private final StudentProfileService studentProfileService;

    public AdminStudentController(StudentProfileService studentProfileService) {
        this.studentProfileService = studentProfileService;
    }

    @GetMapping
    public ApiResponse<List<StudentProfileDetailVO>> listStudents() {
        return ApiResponse.success(studentProfileService.listAdminProfiles());
    }

    @GetMapping("/{id}")
    public ApiResponse<StudentProfileDetailVO> getStudent(@PathVariable Long id) {
        return ApiResponse.success(studentProfileService.getAdminProfile(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<StudentProfileDetailVO> updateStudent(@PathVariable Long id,
                                                             @Valid @RequestBody StudentProfileUpdateRequest request) {
        return ApiResponse.success("学生档案更新成功", studentProfileService.updateAdminProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStudent(@PathVariable Long id) {
        studentProfileService.disableAdminProfile(id);
        return ApiResponse.success("学生档案已禁用", null);
    }
}
