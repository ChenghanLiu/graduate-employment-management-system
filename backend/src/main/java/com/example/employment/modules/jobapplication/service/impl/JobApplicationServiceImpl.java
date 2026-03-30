package com.example.employment.modules.jobapplication.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.JobApplicationStatusEnum;
import com.example.employment.common.enums.JobPostStatusEnum;
import com.example.employment.entity.EnterpriseProfile;
import com.example.employment.entity.JobApplication;
import com.example.employment.entity.JobPost;
import com.example.employment.entity.Resume;
import com.example.employment.entity.StudentProfile;
import com.example.employment.mapper.EnterpriseProfileMapper;
import com.example.employment.mapper.JobApplicationMapper;
import com.example.employment.mapper.JobPostMapper;
import com.example.employment.mapper.ResumeMapper;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.modules.jobapplication.dto.JobApplicationCreateRequest;
import com.example.employment.modules.jobapplication.dto.JobApplicationStatusUpdateRequest;
import com.example.employment.modules.jobapplication.service.JobApplicationService;
import com.example.employment.modules.jobapplication.vo.JobApplicationDetailVO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationMapper jobApplicationMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final ResumeMapper resumeMapper;
    private final JobPostMapper jobPostMapper;
    private final EnterpriseProfileMapper enterpriseProfileMapper;

    public JobApplicationServiceImpl(JobApplicationMapper jobApplicationMapper,
                                     StudentProfileMapper studentProfileMapper,
                                     ResumeMapper resumeMapper,
                                     JobPostMapper jobPostMapper,
                                     EnterpriseProfileMapper enterpriseProfileMapper) {
        this.jobApplicationMapper = jobApplicationMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.resumeMapper = resumeMapper;
        this.jobPostMapper = jobPostMapper;
        this.enterpriseProfileMapper = enterpriseProfileMapper;
    }

    @Override
    public JobApplicationDetailVO createApplication(Long studentUserId, JobApplicationCreateRequest request) {
        StudentProfile studentProfile = getStudentProfileOrThrow(studentUserId);
        Resume resume = getCurrentResumeOrThrow(studentProfile.getId());
        JobPost jobPost = getApplicableJobPostOrThrow(request.getJobPostId());
        EnterpriseProfile enterpriseProfile = getEnterpriseProfileById(jobPost.getEnterpriseId());

        JobApplication existing = jobApplicationMapper.selectOne(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getJobPostId, jobPost.getId())
                .eq(JobApplication::getStudentId, studentProfile.getId())
                .eq(JobApplication::getStatus, 1)
                .last("limit 1"));
        if (existing != null) {
            throw new BusinessException(4090, "请勿重复投递同一岗位");
        }

        JobApplication application = new JobApplication();
        application.setJobPostId(jobPost.getId());
        application.setStudentId(studentProfile.getId());
        application.setResumeId(resume.getId());
        application.setApplicationStatus(JobApplicationStatusEnum.APPLIED.getCode());
        application.setApplyTime(LocalDateTime.now());
        application.setRemark(trimToNull(request.getRemark()));
        application.setStatus(1);
        jobApplicationMapper.insert(application);
        return toDetailVO(application, jobPost, enterpriseProfile, studentProfile, resume);
    }

    @Override
    public List<JobApplicationDetailVO> listMyApplications(Long studentUserId) {
        StudentProfile studentProfile = getStudentProfileOrThrow(studentUserId);
        List<JobApplication> applications = jobApplicationMapper.selectList(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getStudentId, studentProfile.getId())
                .eq(JobApplication::getStatus, 1)
                .orderByDesc(JobApplication::getId));
        return buildDetailList(applications);
    }

    @Override
    public List<JobApplicationDetailVO> listReceivedApplications(Long enterpriseUserId) {
        EnterpriseProfile enterpriseProfile = getEnterpriseProfileByUserIdOrThrow(enterpriseUserId);
        List<Long> jobPostIds = jobPostMapper.selectList(new LambdaQueryWrapper<JobPost>()
                        .select(JobPost::getId)
                        .eq(JobPost::getEnterpriseId, enterpriseProfile.getId())
                        .eq(JobPost::getStatus, 1))
                .stream()
                .map(JobPost::getId)
                .toList();
        if (jobPostIds.isEmpty()) {
            return List.of();
        }
        List<JobApplication> applications = jobApplicationMapper.selectList(new LambdaQueryWrapper<JobApplication>()
                .in(JobApplication::getJobPostId, jobPostIds)
                .eq(JobApplication::getStatus, 1)
                .orderByDesc(JobApplication::getId));
        return buildDetailList(applications);
    }

    @Override
    public JobApplicationDetailVO updateApplicationStatus(Long enterpriseUserId, Long applicationId,
                                                          JobApplicationStatusUpdateRequest request) {
        EnterpriseProfile enterpriseProfile = getEnterpriseProfileByUserIdOrThrow(enterpriseUserId);
        JobApplication application = getApplicationByIdOrThrow(applicationId);
        JobPost jobPost = getJobPostByIdOrThrow(application.getJobPostId());
        if (!enterpriseProfile.getId().equals(jobPost.getEnterpriseId())) {
            throw new BusinessException(4040, "投递记录不存在或无权操作");
        }

        JobApplicationStatusEnum nextStatus = JobApplicationStatusEnum.fromCode(request.getApplicationStatus());
        application.setApplicationStatus(nextStatus.getCode());
        application.setProcessedTime(LocalDateTime.now());
        application.setRemark(trimToNull(request.getRemark()));
        jobApplicationMapper.updateById(application);

        StudentProfile studentProfile = getStudentProfileByIdOrThrow(application.getStudentId());
        Resume resume = getResumeByIdOrThrow(application.getResumeId());
        return toDetailVO(application, jobPost, enterpriseProfile, studentProfile, resume);
    }

    private List<JobApplicationDetailVO> buildDetailList(List<JobApplication> applications) {
        if (applications.isEmpty()) {
            return List.of();
        }

        Map<Long, JobPost> jobPostMap = jobPostMapper.selectBatchIds(extractIds(applications, JobApplication::getJobPostId))
                .stream()
                .collect(Collectors.toMap(JobPost::getId, Function.identity()));
        Map<Long, StudentProfile> studentMap = studentProfileMapper.selectBatchIds(extractIds(applications, JobApplication::getStudentId))
                .stream()
                .collect(Collectors.toMap(StudentProfile::getId, Function.identity()));
        Map<Long, Resume> resumeMap = resumeMapper.selectBatchIds(extractIds(applications, JobApplication::getResumeId))
                .stream()
                .collect(Collectors.toMap(Resume::getId, Function.identity()));
        Map<Long, EnterpriseProfile> enterpriseMap = enterpriseProfileMapper.selectBatchIds(jobPostMap.values().stream()
                        .map(JobPost::getEnterpriseId)
                        .collect(Collectors.toSet()))
                .stream()
                .collect(Collectors.toMap(EnterpriseProfile::getId, Function.identity()));

        return applications.stream()
                .map(application -> {
                    JobPost jobPost = jobPostMap.get(application.getJobPostId());
                    StudentProfile studentProfile = studentMap.get(application.getStudentId());
                    Resume resume = resumeMap.get(application.getResumeId());
                    EnterpriseProfile enterpriseProfile = enterpriseMap.get(jobPost.getEnterpriseId());
                    return toDetailVO(application, jobPost, enterpriseProfile, studentProfile, resume);
                })
                .toList();
    }

    private <T> Set<Long> extractIds(List<JobApplication> applications, Function<JobApplication, Long> idExtractor) {
        return applications.stream().map(idExtractor).collect(Collectors.toSet());
    }

    private StudentProfile getStudentProfileOrThrow(Long studentUserId) {
        StudentProfile studentProfile = studentProfileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getUserId, studentUserId)
                .eq(StudentProfile::getStatus, 1)
                .last("limit 1"));
        if (studentProfile == null) {
            throw new BusinessException(4040, "学生档案不存在，请先完善学生档案");
        }
        return studentProfile;
    }

    private StudentProfile getStudentProfileByIdOrThrow(Long studentId) {
        StudentProfile studentProfile = studentProfileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getId, studentId)
                .eq(StudentProfile::getStatus, 1)
                .last("limit 1"));
        if (studentProfile == null) {
            throw new BusinessException(4040, "学生档案不存在");
        }
        return studentProfile;
    }

    private Resume getCurrentResumeOrThrow(Long studentId) {
        Resume resume = resumeMapper.selectOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getStudentId, studentId)
                .eq(Resume::getStatus, 1)
                .last("limit 1"));
        if (resume == null) {
            throw new BusinessException(4040, "当前学生尚未创建简历");
        }
        return resume;
    }

    private Resume getResumeByIdOrThrow(Long resumeId) {
        Resume resume = resumeMapper.selectOne(new LambdaQueryWrapper<Resume>()
                .eq(Resume::getId, resumeId)
                .eq(Resume::getStatus, 1)
                .last("limit 1"));
        if (resume == null) {
            throw new BusinessException(4040, "简历不存在");
        }
        return resume;
    }

    private JobPost getApplicableJobPostOrThrow(Long jobPostId) {
        JobPost jobPost = getJobPostByIdOrThrow(jobPostId);
        JobPostStatusEnum postStatusEnum = JobPostStatusEnum.fromCode(jobPost.getPostStatus());
        if (!postStatusEnum.isApplicable(jobPost.getDeadline())) {
            throw new BusinessException(4000, "当前岗位不可投递");
        }
        return jobPost;
    }

    private JobPost getJobPostByIdOrThrow(Long jobPostId) {
        JobPost jobPost = jobPostMapper.selectOne(new LambdaQueryWrapper<JobPost>()
                .eq(JobPost::getId, jobPostId)
                .eq(JobPost::getStatus, 1)
                .last("limit 1"));
        if (jobPost == null) {
            throw new BusinessException(4040, "岗位不存在");
        }
        return jobPost;
    }

    private EnterpriseProfile getEnterpriseProfileById(Long enterpriseId) {
        EnterpriseProfile enterpriseProfile = enterpriseProfileMapper.selectOne(new LambdaQueryWrapper<EnterpriseProfile>()
                .eq(EnterpriseProfile::getId, enterpriseId)
                .eq(EnterpriseProfile::getStatus, 1)
                .last("limit 1"));
        if (enterpriseProfile == null) {
            throw new BusinessException(4040, "企业档案不存在");
        }
        return enterpriseProfile;
    }

    private EnterpriseProfile getEnterpriseProfileByUserIdOrThrow(Long enterpriseUserId) {
        EnterpriseProfile enterpriseProfile = enterpriseProfileMapper.selectOne(new LambdaQueryWrapper<EnterpriseProfile>()
                .eq(EnterpriseProfile::getUserId, enterpriseUserId)
                .eq(EnterpriseProfile::getStatus, 1)
                .last("limit 1"));
        if (enterpriseProfile == null) {
            throw new BusinessException(4040, "企业档案不存在，请先完善企业档案");
        }
        return enterpriseProfile;
    }

    private JobApplication getApplicationByIdOrThrow(Long applicationId) {
        JobApplication application = jobApplicationMapper.selectOne(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getId, applicationId)
                .eq(JobApplication::getStatus, 1)
                .last("limit 1"));
        if (application == null) {
            throw new BusinessException(4040, "投递记录不存在");
        }
        return application;
    }

    private JobApplicationDetailVO toDetailVO(JobApplication application, JobPost jobPost, EnterpriseProfile enterpriseProfile,
                                              StudentProfile studentProfile, Resume resume) {
        JobApplicationStatusEnum statusEnum = JobApplicationStatusEnum.fromCode(application.getApplicationStatus());
        return JobApplicationDetailVO.builder()
                .id(application.getId())
                .jobPostId(application.getJobPostId())
                .jobTitle(jobPost.getJobTitle())
                .enterpriseId(enterpriseProfile.getId())
                .enterpriseName(enterpriseProfile.getEnterpriseName())
                .studentId(studentProfile.getId())
                .studentNo(studentProfile.getStudentNo())
                .collegeName(studentProfile.getCollegeName())
                .majorName(studentProfile.getMajorName())
                .resumeId(resume.getId())
                .resumeName(resume.getResumeName())
                .applicationStatus(application.getApplicationStatus())
                .applicationStatusName(statusEnum.getDescription())
                .applyTime(application.getApplyTime())
                .processedTime(application.getProcessedTime())
                .remark(application.getRemark())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
