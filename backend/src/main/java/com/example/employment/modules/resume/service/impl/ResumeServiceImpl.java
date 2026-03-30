package com.example.employment.modules.resume.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.entity.Resume;
import com.example.employment.entity.ResumeEducation;
import com.example.employment.entity.ResumeProject;
import com.example.employment.entity.ResumeSkill;
import com.example.employment.entity.StudentProfile;
import com.example.employment.mapper.ResumeEducationMapper;
import com.example.employment.mapper.ResumeMapper;
import com.example.employment.mapper.ResumeProjectMapper;
import com.example.employment.mapper.ResumeSkillMapper;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.modules.resume.dto.ResumeCreateRequest;
import com.example.employment.modules.resume.dto.ResumeUpdateRequest;
import com.example.employment.modules.resume.service.ResumeService;
import com.example.employment.modules.resume.vo.ResumeDetailVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeMapper resumeMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final ResumeEducationMapper resumeEducationMapper;
    private final ResumeProjectMapper resumeProjectMapper;
    private final ResumeSkillMapper resumeSkillMapper;

    public ResumeServiceImpl(ResumeMapper resumeMapper,
                             StudentProfileMapper studentProfileMapper,
                             ResumeEducationMapper resumeEducationMapper,
                             ResumeProjectMapper resumeProjectMapper,
                             ResumeSkillMapper resumeSkillMapper) {
        this.resumeMapper = resumeMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.resumeEducationMapper = resumeEducationMapper;
        this.resumeProjectMapper = resumeProjectMapper;
        this.resumeSkillMapper = resumeSkillMapper;
    }

    @Override
    public ResumeDetailVO getCurrentResume(Long userId) {
        Resume resume = getCurrentResumeEntityOrThrow(userId);
        refreshCompletionRate(resume.getId());
        return toDetailVO(getResumeById(resume.getId()));
    }

    @Override
    public ResumeDetailVO createCurrentResume(Long userId, ResumeCreateRequest request) {
        StudentProfile studentProfile = getStudentProfileOrThrow(userId);
        Resume existing = getResumeByStudentId(studentProfile.getId());
        if (existing != null) {
            throw new BusinessException(4090, "当前学生已创建简历");
        }

        Resume resume = new Resume();
        resume.setStudentId(studentProfile.getId());
        fillResume(resume, request);
        resume.setCompletionRate(calculateCompletionRate(resume.getId(), resume));
        resume.setIsDefault(1);
        resume.setStatus(1);
        resumeMapper.insert(resume);
        return toDetailVO(resume);
    }

    @Override
    public ResumeDetailVO updateCurrentResume(Long userId, ResumeUpdateRequest request) {
        Resume resume = getCurrentResumeEntityOrThrow(userId);
        fillResume(resume, request);
        resume.setCompletionRate(calculateCompletionRate(resume.getId(), resume));
        resumeMapper.updateById(resume);
        return toDetailVO(resume);
    }

    @Override
    public Resume getCurrentResumeEntityOrNull(Long userId) {
        StudentProfile studentProfile = getStudentProfileOrThrow(userId);
        return getResumeByStudentId(studentProfile.getId());
    }

    @Override
    public Resume getCurrentResumeEntityOrThrow(Long userId) {
        Resume resume = getCurrentResumeEntityOrNull(userId);
        if (resume == null) {
            throw new BusinessException(4040, "当前学生尚未创建简历");
        }
        return resume;
    }

    @Override
    public void refreshCompletionRate(Long resumeId) {
        Resume resume = getResumeById(resumeId);
        if (resume == null) {
            throw new BusinessException(4040, "简历不存在");
        }
        resume.setCompletionRate(calculateCompletionRate(resumeId, resume));
        resumeMapper.updateById(resume);
    }

    private StudentProfile getStudentProfileOrThrow(Long userId) {
        StudentProfile studentProfile = studentProfileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getUserId, userId)
                .eq(StudentProfile::getStatus, 1)
                .last("limit 1"));
        if (studentProfile == null) {
            throw new BusinessException(4040, "学生档案不存在，请先完善学生档案");
        }
        return studentProfile;
    }

    private Resume getResumeByStudentId(Long studentId) {
        return resumeMapper.selectOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getStudentId, studentId)
                .eq(Resume::getStatus, 1)
                .last("limit 1"));
    }

    private Resume getResumeById(Long resumeId) {
        return resumeMapper.selectOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getId, resumeId)
                .eq(Resume::getStatus, 1)
                .last("limit 1"));
    }

    private void fillResume(Resume resume, ResumeCreateRequest request) {
        resume.setResumeName(request.getResumeName().trim());
        resume.setJobIntention(trimToNull(request.getJobIntention()));
        resume.setExpectedCity(trimToNull(request.getExpectedCity()));
        resume.setExpectedSalary(trimToNull(request.getExpectedSalary()));
        resume.setSelfEvaluation(trimToNull(request.getSelfEvaluation()));
    }

    private int calculateCompletionRate(Long resumeId, Resume resume) {
        int score = 20;
        if (StringUtils.hasText(resume.getJobIntention())) {
            score += 15;
        }
        if (StringUtils.hasText(resume.getExpectedCity())) {
            score += 10;
        }
        if (StringUtils.hasText(resume.getExpectedSalary())) {
            score += 10;
        }
        if (StringUtils.hasText(resume.getSelfEvaluation())) {
            score += 20;
        }
        if (resumeId != null && countActiveEducations(resumeId) > 0) {
            score += 10;
        }
        if (resumeId != null && countActiveProjects(resumeId) > 0) {
            score += 7;
        }
        if (resumeId != null && countActiveSkills(resumeId) > 0) {
            score += 8;
        }
        return Math.min(score, 100);
    }

    private long countActiveEducations(Long resumeId) {
        return resumeEducationMapper.selectCount(new LambdaQueryWrapper<ResumeEducation>()
                .eq(ResumeEducation::getResumeId, resumeId)
                .eq(ResumeEducation::getStatus, 1));
    }

    private long countActiveProjects(Long resumeId) {
        return resumeProjectMapper.selectCount(new LambdaQueryWrapper<ResumeProject>()
                .eq(ResumeProject::getResumeId, resumeId)
                .eq(ResumeProject::getStatus, 1));
    }

    private long countActiveSkills(Long resumeId) {
        return resumeSkillMapper.selectCount(new LambdaQueryWrapper<ResumeSkill>()
                .eq(ResumeSkill::getResumeId, resumeId)
                .eq(ResumeSkill::getStatus, 1));
    }

    private ResumeDetailVO toDetailVO(Resume resume) {
        return ResumeDetailVO.builder()
                .id(resume.getId())
                .studentId(resume.getStudentId())
                .resumeName(resume.getResumeName())
                .jobIntention(resume.getJobIntention())
                .expectedCity(resume.getExpectedCity())
                .expectedSalary(resume.getExpectedSalary())
                .selfEvaluation(resume.getSelfEvaluation())
                .completionRate(resume.getCompletionRate())
                .isDefault(resume.getIsDefault())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
