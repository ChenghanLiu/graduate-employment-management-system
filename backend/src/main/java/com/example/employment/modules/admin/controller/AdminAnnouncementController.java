package com.example.employment.modules.admin.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/admin/announcements")
@RequireAnyRole(RoleCode.ADMIN)
public class AdminAnnouncementController {

    private final AnnouncementService announcementService;
    private final CurrentUserResolver currentUserResolver;

    public AdminAnnouncementController(AnnouncementService announcementService,
                                       CurrentUserResolver currentUserResolver) {
        this.announcementService = announcementService;
        this.currentUserResolver = currentUserResolver;
    }

    @GetMapping
    public ApiResponse<List<AnnouncementDetailVO>> listAnnouncements() {
        return ApiResponse.success(announcementService.listAdminAnnouncements(currentUserResolver.resolveAdminUserId()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AnnouncementDetailVO> getAnnouncement(@PathVariable Long id) {
        return ApiResponse.success(announcementService.getAnnouncementDetail(id));
    }

    @PostMapping
    public ApiResponse<AnnouncementDetailVO> createAnnouncement(@Valid @RequestBody AnnouncementCreateRequest request) {
        Long adminUserId = currentUserResolver.resolveAdminUserId();
        return ApiResponse.success("公告创建成功", announcementService.createAnnouncement(adminUserId, request));
    }

    @PutMapping("/{id}")
    public ApiResponse<AnnouncementDetailVO> updateAnnouncement(@PathVariable Long id,
                                                                @Valid @RequestBody AnnouncementUpdateRequest request) {
        Long adminUserId = currentUserResolver.resolveAdminUserId();
        return ApiResponse.success("公告更新成功", announcementService.updateAnnouncement(adminUserId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(currentUserResolver.resolveAdminUserId(), id);
        return ApiResponse.success("公告已下线", null);
    }

    @PutMapping("/{id}/status")
    public ApiResponse<AnnouncementDetailVO> updateAnnouncementStatus(@PathVariable Long id,
                                                                      @Valid @RequestBody AnnouncementStatusUpdateRequest request) {
        Long adminUserId = currentUserResolver.resolveAdminUserId();
        return ApiResponse.success("公告状态更新成功", announcementService.updateAnnouncementStatus(adminUserId, id, request));
    }
}
