package com.example.employment.modules.resume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.entity.Resume;
import com.example.employment.entity.ResumeProject;
import com.example.employment.mapper.ResumeProjectMapper;
import com.example.employment.modules.resume.dto.ResumeProjectCreateRequest;
import com.example.employment.modules.resume.dto.ResumeProjectUpdateRequest;
import com.example.employment.modules.resume.service.ResumeProjectService;
import com.example.employment.modules.resume.service.ResumeService;
import com.example.employment.modules.resume.vo.ResumeProjectVO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ResumeProjectServiceImpl implements ResumeProjectService {

    private final ResumeProjectMapper resumeProjectMapper;
    private final ResumeService resumeService;

    public ResumeProjectServiceImpl(ResumeProjectMapper resumeProjectMapper, ResumeService resumeService) {
        this.resumeProjectMapper = resumeProjectMapper;
        this.resumeService = resumeService;
    }

    @Override
    public List<ResumeProjectVO> listCurrentProjects(Long userId) {
        Resume resume = resumeService.getCurrentResumeEntityOrNull(userId);
        if (resume == null) {
            return List.of();
        }
        return resumeProjectMapper.selectList(new LambdaQueryWrapper<ResumeProject>()
                        .eq(ResumeProject::getResumeId, resume.getId())
                        .eq(ResumeProject::getStatus, 1)
                        .orderByAsc(ResumeProject::getSortOrder)
                        .orderByDesc(ResumeProject::getId))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public ResumeProjectVO createCurrentProject(Long userId, ResumeProjectCreateRequest request) {
        Resume resume = resumeService.getCurrentResumeEntityOrThrow(userId);
        ResumeProject project = new ResumeProject();
        project.setResumeId(resume.getId());
        fillProject(project, request);
        project.setStatus(1);
        resumeProjectMapper.insert(project);
        resumeService.refreshCompletionRate(resume.getId());
        return toVO(project);
    }

    @Override
    public ResumeProjectVO updateCurrentProject(Long userId, Long id, ResumeProjectUpdateRequest request) {
        ResumeProject project = getOwnedProjectOrThrow(userId, id);
        fillProject(project, request);
        resumeProjectMapper.updateById(project);
        resumeService.refreshCompletionRate(project.getResumeId());
        return toVO(project);
    }

    @Override
    public void deleteCurrentProject(Long userId, Long id) {
        ResumeProject project = getOwnedProjectOrThrow(userId, id);
        project.setStatus(0);
        resumeProjectMapper.updateById(project);
        resumeService.refreshCompletionRate(project.getResumeId());
    }

    private ResumeProject getOwnedProjectOrThrow(Long userId, Long id) {
        Resume resume = resumeService.getCurrentResumeEntityOrThrow(userId);
        ResumeProject project = resumeProjectMapper.selectOne(new LambdaQueryWrapper<ResumeProject>()
                .eq(ResumeProject::getId, id)
                .eq(ResumeProject::getResumeId, resume.getId())
                .eq(ResumeProject::getStatus, 1)
                .last("limit 1"));
        if (project == null) {
            throw new BusinessException(4040, "项目经历不存在");
        }
        return project;
    }

    private void fillProject(ResumeProject project, ResumeProjectCreateRequest request) {
        project.setProjectName(request.getProjectName().trim());
        project.setRoleName(trimToNull(request.getRoleName()));
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setDescription(trimToNull(request.getDescription()));
        project.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    }

    private ResumeProjectVO toVO(ResumeProject project) {
        return ResumeProjectVO.builder()
                .id(project.getId())
                .resumeId(project.getResumeId())
                .projectName(project.getProjectName())
                .roleName(project.getRoleName())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .description(project.getDescription())
                .sortOrder(project.getSortOrder())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
