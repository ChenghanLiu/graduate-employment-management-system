package com.example.employment.modules.resume.service;

import com.example.employment.modules.resume.dto.ResumeProjectCreateRequest;
import com.example.employment.modules.resume.dto.ResumeProjectUpdateRequest;
import com.example.employment.modules.resume.vo.ResumeProjectVO;
import java.util.List;

public interface ResumeProjectService {

    List<ResumeProjectVO> listCurrentProjects(Long userId);

    ResumeProjectVO createCurrentProject(Long userId, ResumeProjectCreateRequest request);

    ResumeProjectVO updateCurrentProject(Long userId, Long id, ResumeProjectUpdateRequest request);

    void deleteCurrentProject(Long userId, Long id);
}
