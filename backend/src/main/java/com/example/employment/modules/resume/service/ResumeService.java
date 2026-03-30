package com.example.employment.modules.resume.service;

import com.example.employment.entity.Resume;
import com.example.employment.modules.resume.dto.ResumeCreateRequest;
import com.example.employment.modules.resume.dto.ResumeUpdateRequest;
import com.example.employment.modules.resume.vo.ResumeDetailVO;

public interface ResumeService {

    ResumeDetailVO getCurrentResume(Long userId);

    ResumeDetailVO createCurrentResume(Long userId, ResumeCreateRequest request);

    ResumeDetailVO updateCurrentResume(Long userId, ResumeUpdateRequest request);

    Resume getCurrentResumeEntityOrNull(Long userId);

    Resume getCurrentResumeEntityOrThrow(Long userId);

    void refreshCompletionRate(Long resumeId);
}
