package com.example.employment.modules.announcement.controller;

import com.example.employment.common.ApiResponse;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.modules.announcement.dto.AnnouncementCreateRequest;
import com.example.employment.modules.announcement.dto.AnnouncementStatusUpdateRequest;
import com.example.employment.modules.announcement.dto.AnnouncementUpdateRequest;
import com.example.employment.modules.announcement.service.AnnouncementService;
import com.example.employment.modules.announcement.vo.AnnouncementDetailVO;
import com.example.employment.security.RequireAnyRole;
import com.example.employment.web.CurrentUserResolver;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final CurrentUserResolver currentUserResolver;

    public AnnouncementController(AnnouncementService announcementService, CurrentUserResolver currentUserResolver) {
        this.announcementService = announcementService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping("/manage")
    @RequireAnyRole({RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<List<AnnouncementDetailVO>> listManageAnnouncements() {
        Long managerUserId = currentUserResolver.resolveTeacherUserId();
        return ApiResponse.success(announcementService.listManageAnnouncements(managerUserId));
    }

    @PostMapping
    @RequireAnyRole({RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<AnnouncementDetailVO> createAnnouncement(
            @Valid @RequestBody AnnouncementCreateRequest createRequest) {
        Long managerUserId = currentUserResolver.resolveTeacherUserId();
        return ApiResponse.success("公告创建成功", announcementService.createAnnouncement(managerUserId, createRequest));
    }

    @PutMapping("/{id}")
    @RequireAnyRole({RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<AnnouncementDetailVO> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementUpdateRequest updateRequest) {
        Long managerUserId = currentUserResolver.resolveTeacherUserId();
        return ApiResponse.success("公告更新成功", announcementService.updateAnnouncement(managerUserId, id, updateRequest));
    }

    @PutMapping("/{id}/status")
    @RequireAnyRole({RoleCode.EMPLOYMENT_TEACHER, RoleCode.ADMIN})
    public ApiResponse<AnnouncementDetailVO> updateAnnouncementStatus(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementStatusUpdateRequest updateRequest) {
        Long managerUserId = currentUserResolver.resolveTeacherUserId();
        return ApiResponse.success("公告状态更新成功",
                announcementService.updateAnnouncementStatus(managerUserId, id, updateRequest));
    }

    @GetMapping
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<List<AnnouncementDetailVO>> listPublishedAnnouncements() {
        return ApiResponse.success(announcementService.listPublishedAnnouncements());
    }

    @GetMapping("/{id}")
    @RequireAnyRole(RoleCode.STUDENT)
    public ApiResponse<AnnouncementDetailVO> getPublishedAnnouncementDetail(@PathVariable Long id) {
        return ApiResponse.success(announcementService.getPublishedAnnouncementDetail(id));
    }
}
