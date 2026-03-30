package com.example.employment.modules.resume.service;

import com.example.employment.modules.resume.dto.ResumeEducationCreateRequest;
import com.example.employment.modules.resume.dto.ResumeEducationUpdateRequest;
import com.example.employment.modules.resume.vo.ResumeEducationVO;
import java.util.List;

public interface ResumeEducationService {

    List<ResumeEducationVO> listCurrentEducations(Long userId);

    ResumeEducationVO createCurrentEducation(Long userId, ResumeEducationCreateRequest request);

    ResumeEducationVO updateCurrentEducation(Long userId, Long id, ResumeEducationUpdateRequest request);

    void deleteCurrentEducation(Long userId, Long id);
}
