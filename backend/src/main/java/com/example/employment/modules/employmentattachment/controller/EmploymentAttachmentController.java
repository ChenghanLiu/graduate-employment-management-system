package com.example.employment.modules.employmentattachment.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.employmentattachment.dto.EmploymentAttachmentCreateRequest;
import com.example.employment.modules.employmentattachment.service.EmploymentAttachmentService;
import com.example.employment.modules.employmentattachment.vo.EmploymentAttachmentVO;
import com.example.employment.security.RequireAnyRole;
import com.example.employment.web.CurrentUserResolver;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/employment-records/current/attachments")
@RequireAnyRole(RoleCode.STUDENT)
public class EmploymentAttachmentController {

    private final EmploymentAttachmentService employmentAttachmentService;
    private final CurrentUserResolver currentUserResolver;

    public EmploymentAttachmentController(EmploymentAttachmentService employmentAttachmentService,
                                          CurrentUserResolver currentUserResolver) {
        this.employmentAttachmentService = employmentAttachmentService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ApiResponse<List<EmploymentAttachmentVO>> listCurrentAttachments() {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success(employmentAttachmentService.listCurrentAttachments(userId));
    }

    @PostMapping
    public ApiResponse<EmploymentAttachmentVO> addCurrentAttachment(
            @Valid @RequestBody EmploymentAttachmentCreateRequest createRequest) {
        Long userId = currentUserResolver.resolveStudentUserId();
        return ApiResponse.success("就业附件添加成功",
                employmentAttachmentService.addCurrentAttachment(userId, createRequest));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCurrentAttachment(@PathVariable Long id) {
        Long userId = currentUserResolver.resolveStudentUserId();
        employmentAttachmentService.deleteCurrentAttachment(userId, id);
        return ApiResponse.success("就业附件删除成功", null);
    }
}
