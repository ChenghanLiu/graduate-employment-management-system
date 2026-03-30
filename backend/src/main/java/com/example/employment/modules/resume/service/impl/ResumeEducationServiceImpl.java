package com.example.employment.modules.resume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.entity.Resume;
import com.example.employment.entity.ResumeEducation;
import com.example.employment.mapper.ResumeEducationMapper;
import com.example.employment.modules.resume.dto.ResumeEducationCreateRequest;
import com.example.employment.modules.resume.dto.ResumeEducationUpdateRequest;
import com.example.employment.modules.resume.service.ResumeEducationService;
import com.example.employment.modules.resume.service.ResumeService;
import com.example.employment.modules.resume.vo.ResumeEducationVO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ResumeEducationServiceImpl implements ResumeEducationService {

    private final ResumeEducationMapper resumeEducationMapper;
    private final ResumeService resumeService;

    public ResumeEducationServiceImpl(ResumeEducationMapper resumeEducationMapper, ResumeService resumeService) {
        this.resumeEducationMapper = resumeEducationMapper;
        this.resumeService = resumeService;
    }

    @Override
    public List<ResumeEducationVO> listCurrentEducations(Long userId) {
        Resume resume = resumeService.getCurrentResumeEntityOrNull(userId);
        if (resume == null) {
            return List.of();
        }
        return resumeEducationMapper.selectList(new LambdaQueryWrapper<ResumeEducation>()
                        .eq(ResumeEducation::getResumeId, resume.getId())
                        .eq(ResumeEducation::getStatus, 1)
                        .orderByAsc(ResumeEducation::getSortOrder)
                        .orderByDesc(ResumeEducation::getId))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public ResumeEducationVO createCurrentEducation(Long userId, ResumeEducationCreateRequest request) {
        Resume resume = resumeService.getCurrentResumeEntityOrThrow(userId);
        ResumeEducation education = new ResumeEducation();
        education.setResumeId(resume.getId());
        fillEducation(education, request);
        education.setStatus(1);
        resumeEducationMapper.insert(education);
        resumeService.refreshCompletionRate(resume.getId());
        return toVO(education);
    }

    @Override
    public ResumeEducationVO updateCurrentEducation(Long userId, Long id, ResumeEducationUpdateRequest request) {
        ResumeEducation education = getOwnedEducationOrThrow(userId, id);
        fillEducation(education, request);
        resumeEducationMapper.updateById(education);
        resumeService.refreshCompletionRate(education.getResumeId());
        return toVO(education);
    }

    @Override
    public void deleteCurrentEducation(Long userId, Long id) {
        ResumeEducation education = getOwnedEducationOrThrow(userId, id);
        education.setStatus(0);
        resumeEducationMapper.updateById(education);
        resumeService.refreshCompletionRate(education.getResumeId());
    }

    private ResumeEducation getOwnedEducationOrThrow(Long userId, Long id) {
        Resume resume = resumeService.getCurrentResumeEntityOrThrow(userId);
        ResumeEducation education = resumeEducationMapper.selectOne(new LambdaQueryWrapper<ResumeEducation>()
                .eq(ResumeEducation::getId, id)
                .eq(ResumeEducation::getResumeId, resume.getId())
                .eq(ResumeEducation::getStatus, 1)
                .last("limit 1"));
        if (education == null) {
            throw new BusinessException(4040, "教育经历不存在");
        }
        return education;
    }

    private void fillEducation(ResumeEducation education, ResumeEducationCreateRequest request) {
        education.setSchoolName(request.getSchoolName().trim());
        education.setMajorName(request.getMajorName().trim());
        education.setEducationLevel(request.getEducationLevel().trim());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setDescription(trimToNull(request.getDescription()));
        education.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    }

    private ResumeEducationVO toVO(ResumeEducation education) {
        return ResumeEducationVO.builder()
                .id(education.getId())
                .resumeId(education.getResumeId())
                .schoolName(education.getSchoolName())
                .majorName(education.getMajorName())
                .educationLevel(education.getEducationLevel())
                .startDate(education.getStartDate())
                .endDate(education.getEndDate())
                .description(education.getDescription())
                .sortOrder(education.getSortOrder())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
