package com.example.employment.modules.announcement.service;

import com.example.employment.modules.announcement.dto.AnnouncementCreateRequest;
import com.example.employment.modules.announcement.dto.AnnouncementStatusUpdateRequest;
import com.example.employment.modules.announcement.dto.AnnouncementUpdateRequest;
import com.example.employment.modules.announcement.vo.AnnouncementDetailVO;
import java.util.List;

public interface AnnouncementService {

    List<AnnouncementDetailVO> listManageAnnouncements(Long managerUserId);

    List<AnnouncementDetailVO> listAdminAnnouncements(Long adminUserId);

    AnnouncementDetailVO getAnnouncementDetail(Long announcementId);

    AnnouncementDetailVO createAnnouncement(Long managerUserId, AnnouncementCreateRequest request);

    AnnouncementDetailVO updateAnnouncement(Long managerUserId, Long announcementId, AnnouncementUpdateRequest request);

    AnnouncementDetailVO updateAnnouncementStatus(Long managerUserId, Long announcementId, AnnouncementStatusUpdateRequest request);

    void deleteAnnouncement(Long managerUserId, Long announcementId);

    List<AnnouncementDetailVO> listPublishedAnnouncements();

    AnnouncementDetailVO getPublishedAnnouncementDetail(Long announcementId);
}
