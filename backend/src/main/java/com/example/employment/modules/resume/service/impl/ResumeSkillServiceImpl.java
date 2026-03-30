package com.example.employment.modules.resume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.entity.Resume;
import com.example.employment.entity.ResumeSkill;
import com.example.employment.mapper.ResumeSkillMapper;
import com.example.employment.modules.resume.dto.ResumeSkillCreateRequest;
import com.example.employment.modules.resume.dto.ResumeSkillUpdateRequest;
import com.example.employment.modules.resume.service.ResumeService;
import com.example.employment.modules.resume.service.ResumeSkillService;
import com.example.employment.modules.resume.vo.ResumeSkillVO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ResumeSkillServiceImpl implements ResumeSkillService {

    private final ResumeSkillMapper resumeSkillMapper;
    private final ResumeService resumeService;

    public ResumeSkillServiceImpl(ResumeSkillMapper resumeSkillMapper, ResumeService resumeService) {
        this.resumeSkillMapper = resumeSkillMapper;
        this.resumeService = resumeService;
    }

    @Override
    public List<ResumeSkillVO> listCurrentSkills(Long userId) {
        Resume resume = resumeService.getCurrentResumeEntityOrNull(userId);
        if (resume == null) {
            return List.of();
        }
        return resumeSkillMapper.selectList(new LambdaQueryWrapper<ResumeSkill>()
                        .eq(ResumeSkill::getResumeId, resume.getId())
                        .eq(ResumeSkill::getStatus, 1)
                        .orderByAsc(ResumeSkill::getSortOrder)
                        .orderByDesc(ResumeSkill::getId))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public ResumeSkillVO createCurrentSkill(Long userId, ResumeSkillCreateRequest request) {
        Resume resume = resumeService.getCurrentResumeEntityOrThrow(userId);
        ResumeSkill skill = new ResumeSkill();
        skill.setResumeId(resume.getId());
        fillSkill(skill, request);
        skill.setStatus(1);
        resumeSkillMapper.insert(skill);
        resumeService.refreshCompletionRate(resume.getId());
        return toVO(skill);
    }

    @Override
    public ResumeSkillVO updateCurrentSkill(Long userId, Long id, ResumeSkillUpdateRequest request) {
        ResumeSkill skill = getOwnedSkillOrThrow(userId, id);
        fillSkill(skill, request);
        resumeSkillMapper.updateById(skill);
        resumeService.refreshCompletionRate(skill.getResumeId());
        return toVO(skill);
    }

    @Override
    public void deleteCurrentSkill(Long userId, Long id) {
        ResumeSkill skill = getOwnedSkillOrThrow(userId, id);
        skill.setStatus(0);
        resumeSkillMapper.updateById(skill);
        resumeService.refreshCompletionRate(skill.getResumeId());
    }

    private ResumeSkill getOwnedSkillOrThrow(Long userId, Long id) {
        Resume resume = resumeService.getCurrentResumeEntityOrThrow(userId);
        ResumeSkill skill = resumeSkillMapper.selectOne(new LambdaQueryWrapper<ResumeSkill>()
                .eq(ResumeSkill::getId, id)
                .eq(ResumeSkill::getResumeId, resume.getId())
                .eq(ResumeSkill::getStatus, 1)
                .last("limit 1"));
        if (skill == null) {
            throw new BusinessException(4040, "技能不存在");
        }
        return skill;
    }

    private void fillSkill(ResumeSkill skill, ResumeSkillCreateRequest request) {
        skill.setSkillName(request.getSkillName().trim());
        skill.setSkillLevel(request.getSkillLevel());
        skill.setDescription(trimToNull(request.getDescription()));
        skill.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
    }

    private ResumeSkillVO toVO(ResumeSkill skill) {
        return ResumeSkillVO.builder()
                .id(skill.getId())
                .resumeId(skill.getResumeId())
                .skillName(skill.getSkillName())
                .skillLevel(skill.getSkillLevel())
                .description(skill.getDescription())
                .sortOrder(skill.getSortOrder())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
