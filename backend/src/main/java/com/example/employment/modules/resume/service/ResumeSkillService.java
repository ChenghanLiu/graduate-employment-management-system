package com.example.employment.modules.resume.service;

import com.example.employment.modules.resume.dto.ResumeSkillCreateRequest;
import com.example.employment.modules.resume.dto.ResumeSkillUpdateRequest;
import com.example.employment.modules.resume.vo.ResumeSkillVO;
import java.util.List;

public interface ResumeSkillService {

    List<ResumeSkillVO> listCurrentSkills(Long userId);

    ResumeSkillVO createCurrentSkill(Long userId, ResumeSkillCreateRequest request);

    ResumeSkillVO updateCurrentSkill(Long userId, Long id, ResumeSkillUpdateRequest request);

    void deleteCurrentSkill(Long userId, Long id);
}
