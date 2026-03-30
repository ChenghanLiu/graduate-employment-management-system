package com.example.employment.modules.announcement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.ApiCode;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.AnnouncementStatusEnum;
import com.example.employment.entity.Announcement;
import com.example.employment.mapper.AnnouncementMapper;
import com.example.employment.modules.announcement.dto.AnnouncementCreateRequest;
import com.example.employment.modules.announcement.dto.AnnouncementStatusUpdateRequest;
import com.example.employment.modules.announcement.dto.AnnouncementUpdateRequest;
import com.example.employment.modules.announcement.service.AnnouncementService;
import com.example.employment.modules.announcement.vo.AnnouncementDetailVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementMapper announcementMapper;

    public AnnouncementServiceImpl(AnnouncementMapper announcementMapper) {
        this.announcementMapper = announcementMapper;
    }

    @Override
    public List<AnnouncementDetailVO> listManageAnnouncements(Long managerUserId) {
        requireManager(managerUserId);
        return announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                        .orderByDesc(Announcement::getCreateTime)
                        .orderByDesc(Announcement::getId))
                .stream()
                .map(this::toDetailVO)
                .toList();
    }

    @Override
    public List<AnnouncementDetailVO> listAdminAnnouncements(Long adminUserId) {
        return listManageAnnouncements(adminUserId);
    }

    @Override
    public AnnouncementDetailVO getAnnouncementDetail(Long announcementId) {
        return toDetailVO(getAnnouncementOrThrow(announcementId));
    }

    @Override
    public AnnouncementDetailVO createAnnouncement(Long managerUserId, AnnouncementCreateRequest request) {
        requireManager(managerUserId);
        Announcement announcement = new Announcement();
        fillAnnouncement(announcement, request.getTitle(), request.getContent(), request.getType());
        announcement.setStatus(AnnouncementStatusEnum.OFFLINE.getCode());
        announcementMapper.insert(announcement);
        return toDetailVO(announcement);
    }

    @Override
    public AnnouncementDetailVO updateAnnouncement(Long managerUserId, Long announcementId, AnnouncementUpdateRequest request) {
        requireManager(managerUserId);
        Announcement announcement = getAnnouncementOrThrow(announcementId);
        fillAnnouncement(announcement, request.getTitle(), request.getContent(), request.getType());
        announcementMapper.updateById(announcement);
        return toDetailVO(announcement);
    }

    @Override
    public AnnouncementDetailVO updateAnnouncementStatus(Long managerUserId, Long announcementId,
                                                         AnnouncementStatusUpdateRequest request) {
        requireManager(managerUserId);
        Announcement announcement = getAnnouncementOrThrow(announcementId);
        AnnouncementStatusEnum nextStatus = AnnouncementStatusEnum.fromCode(request.getStatus());
        announcement.setStatus(nextStatus.getCode());
        announcement.setPublishTime(nextStatus == AnnouncementStatusEnum.PUBLISHED ? LocalDateTime.now() : null);
        announcementMapper.updateById(announcement);
        return toDetailVO(announcement);
    }

    @Override
    public List<AnnouncementDetailVO> listPublishedAnnouncements() {
        return announcementMapper.selectList(new LambdaQueryWrapper<Announcement>()
                        .eq(Announcement::getStatus, AnnouncementStatusEnum.PUBLISHED.getCode())
                        .orderByDesc(Announcement::getPublishTime)
                        .orderByDesc(Announcement::getId))
                .stream()
                .map(this::toDetailVO)
                .toList();
    }

    @Override
    public AnnouncementDetailVO getPublishedAnnouncementDetail(Long announcementId) {
        Announcement announcement = announcementMapper.selectOne(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getId, announcementId)
                .eq(Announcement::getStatus, AnnouncementStatusEnum.PUBLISHED.getCode())
                .last("limit 1"));
        if (announcement == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "公告不存在");
        }
        return toDetailVO(announcement);
    }

    @Override
    public void deleteAnnouncement(Long managerUserId, Long announcementId) {
        requireManager(managerUserId);
        Announcement announcement = getAnnouncementOrThrow(announcementId);
        announcement.setStatus(AnnouncementStatusEnum.OFFLINE.getCode());
        announcement.setPublishTime(null);
        announcementMapper.updateById(announcement);
    }

    private void requireManager(Long managerUserId) {
        if (managerUserId == null || managerUserId <= 0) {
            throw new BusinessException(4001, "管理用户不存在");
        }
    }

    private Announcement getAnnouncementOrThrow(Long announcementId) {
        Announcement announcement = announcementMapper.selectOne(new LambdaQueryWrapper<Announcement>()
                .eq(Announcement::getId, announcementId)
                .last("limit 1"));
        if (announcement == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "公告不存在");
        }
        return announcement;
    }

    private void fillAnnouncement(Announcement announcement, String title, String content, String type) {
        announcement.setTitle(title.trim());
        announcement.setContent(content.trim());
        announcement.setType(type.trim());
    }

    private AnnouncementDetailVO toDetailVO(Announcement announcement) {
        AnnouncementStatusEnum statusEnum = AnnouncementStatusEnum.fromCode(announcement.getStatus());
        return AnnouncementDetailVO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .type(announcement.getType())
                .status(announcement.getStatus())
                .statusName(statusEnum.getDescription())
                .publishTime(announcement.getPublishTime())
                .createTime(announcement.getCreateTime())
                .updateTime(announcement.getUpdateTime())
                .build();
    }
}
