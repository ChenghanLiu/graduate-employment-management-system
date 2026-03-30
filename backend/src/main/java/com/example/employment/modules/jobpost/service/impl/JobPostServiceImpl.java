package com.example.employment.modules.jobpost.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.JobPostStatusEnum;
import com.example.employment.entity.EnterpriseProfile;
import com.example.employment.entity.JobPost;
import com.example.employment.mapper.EnterpriseProfileMapper;
import com.example.employment.mapper.JobPostMapper;
import com.example.employment.modules.jobpost.dto.JobPostCreateRequest;
import com.example.employment.modules.jobpost.dto.JobPostStatusUpdateRequest;
import com.example.employment.modules.jobpost.dto.JobPostUpdateRequest;
import com.example.employment.modules.jobpost.service.JobPostService;
import com.example.employment.modules.jobpost.vo.JobPostDetailVO;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JobPostServiceImpl implements JobPostService {

    private final JobPostMapper jobPostMapper;
    private final EnterpriseProfileMapper enterpriseProfileMapper;

    public JobPostServiceImpl(JobPostMapper jobPostMapper, EnterpriseProfileMapper enterpriseProfileMapper) {
        this.jobPostMapper = jobPostMapper;
        this.enterpriseProfileMapper = enterpriseProfileMapper;
    }

    @Override
    public List<JobPostDetailVO> listMyJobPosts(Long enterpriseUserId) {
        EnterpriseProfile enterpriseProfile = getEnterpriseProfileOrThrow(enterpriseUserId);
        return jobPostMapper.selectList(new LambdaQueryWrapper<JobPost>()
                        .eq(JobPost::getEnterpriseId, enterpriseProfile.getId())
                        .eq(JobPost::getStatus, 1)
                        .orderByDesc(JobPost::getId))
                .stream()
                .map(item -> toDetailVO(item, enterpriseProfile))
                .toList();
    }

    @Override
    public JobPostDetailVO createJobPost(Long enterpriseUserId, JobPostCreateRequest request) {
        EnterpriseProfile enterpriseProfile = getEnterpriseProfileOrThrow(enterpriseUserId);
        validateSalaryRange(request.getSalaryMin(), request.getSalaryMax());

        JobPost jobPost = new JobPost();
        jobPost.setEnterpriseId(enterpriseProfile.getId());
        fillJobPost(jobPost, request);
        jobPost.setPostStatus(JobPostStatusEnum.DRAFT.getCode());
        jobPost.setStatus(1);
        jobPostMapper.insert(jobPost);
        return toDetailVO(jobPost, enterpriseProfile);
    }

    @Override
    public JobPostDetailVO updateJobPost(Long enterpriseUserId, Long jobPostId, JobPostUpdateRequest request) {
        EnterpriseProfile enterpriseProfile = getEnterpriseProfileOrThrow(enterpriseUserId);
        validateSalaryRange(request.getSalaryMin(), request.getSalaryMax());

        JobPost jobPost = getOwnJobPostOrThrow(enterpriseProfile.getId(), jobPostId);
        fillJobPost(jobPost, request);
        jobPostMapper.updateById(jobPost);
        return toDetailVO(jobPost, enterpriseProfile);
    }

    @Override
    public JobPostDetailVO updateJobPostStatus(Long enterpriseUserId, Long jobPostId, JobPostStatusUpdateRequest request) {
        EnterpriseProfile enterpriseProfile = getEnterpriseProfileOrThrow(enterpriseUserId);
        JobPost jobPost = getOwnJobPostOrThrow(enterpriseProfile.getId(), jobPostId);
        JobPostStatusEnum nextStatus = JobPostStatusEnum.fromCode(request.getPostStatus());
        jobPost.setPostStatus(nextStatus.getCode());
        jobPostMapper.updateById(jobPost);
        return toDetailVO(jobPost, enterpriseProfile);
    }

    @Override
    public List<JobPostDetailVO> listOpenJobPosts() {
        return jobPostMapper.selectList(new LambdaQueryWrapper<JobPost>()
                        .eq(JobPost::getStatus, 1)
                        .eq(JobPost::getPostStatus, JobPostStatusEnum.RECRUITING.getCode())
                        .orderByDesc(JobPost::getId))
                .stream()
                .filter(this::isApplicable)
                .map(jobPost -> toDetailVO(jobPost, getEnterpriseProfileById(jobPost.getEnterpriseId())))
                .toList();
    }

    @Override
    public JobPostDetailVO getOpenJobPostDetail(Long jobPostId) {
        JobPost jobPost = jobPostMapper.selectOne(new LambdaQueryWrapper<JobPost>()
                .eq(JobPost::getId, jobPostId)
                .eq(JobPost::getStatus, 1)
                .last("limit 1"));
        if (jobPost == null || !isApplicable(jobPost)) {
            throw new BusinessException(4040, "岗位不存在或当前不可投递");
        }
        return toDetailVO(jobPost, getEnterpriseProfileById(jobPost.getEnterpriseId()));
    }

    private EnterpriseProfile getEnterpriseProfileOrThrow(Long enterpriseUserId) {
        EnterpriseProfile enterpriseProfile = enterpriseProfileMapper.selectOne(new LambdaQueryWrapper<EnterpriseProfile>()
                .eq(EnterpriseProfile::getUserId, enterpriseUserId)
                .eq(EnterpriseProfile::getStatus, 1)
                .last("limit 1"));
        if (enterpriseProfile == null) {
            throw new BusinessException(4040, "企业档案不存在，请先完善企业档案");
        }
        return enterpriseProfile;
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

    private JobPost getOwnJobPostOrThrow(Long enterpriseId, Long jobPostId) {
        JobPost jobPost = jobPostMapper.selectOne(new LambdaQueryWrapper<JobPost>()
                .eq(JobPost::getId, jobPostId)
                .eq(JobPost::getEnterpriseId, enterpriseId)
                .eq(JobPost::getStatus, 1)
                .last("limit 1"));
        if (jobPost == null) {
            throw new BusinessException(4040, "岗位不存在或无权操作");
        }
        return jobPost;
    }

    private void fillJobPost(JobPost jobPost, JobPostCreateRequest request) {
        jobPost.setJobTitle(request.getJobTitle().trim());
        jobPost.setJobCategory(trimToNull(request.getJobCategory()));
        jobPost.setWorkCity(request.getWorkCity().trim());
        jobPost.setEducationRequirement(trimToNull(request.getEducationRequirement()));
        jobPost.setSalaryMin(request.getSalaryMin());
        jobPost.setSalaryMax(request.getSalaryMax());
        jobPost.setSalaryRemark(trimToNull(request.getSalaryRemark()));
        jobPost.setHiringCount(request.getHiringCount() == null ? 1 : request.getHiringCount());
        jobPost.setJobDescription(trimToNull(request.getJobDescription()));
        jobPost.setDeadline(request.getDeadline());
    }

    private boolean isApplicable(JobPost jobPost) {
        return JobPostStatusEnum.fromCode(jobPost.getPostStatus()).isApplicable(jobPost.getDeadline());
    }

    private void validateSalaryRange(BigDecimal salaryMin, BigDecimal salaryMax) {
        if (salaryMin != null && salaryMax != null && salaryMin.compareTo(salaryMax) > 0) {
            throw new BusinessException(4001, "最低薪资不能高于最高薪资");
        }
    }

    private JobPostDetailVO toDetailVO(JobPost jobPost, EnterpriseProfile enterpriseProfile) {
        JobPostStatusEnum postStatusEnum = JobPostStatusEnum.fromCode(jobPost.getPostStatus());
        return JobPostDetailVO.builder()
                .id(jobPost.getId())
                .enterpriseId(jobPost.getEnterpriseId())
                .enterpriseName(enterpriseProfile.getEnterpriseName())
                .jobTitle(jobPost.getJobTitle())
                .jobCategory(jobPost.getJobCategory())
                .workCity(jobPost.getWorkCity())
                .educationRequirement(jobPost.getEducationRequirement())
                .salaryMin(jobPost.getSalaryMin())
                .salaryMax(jobPost.getSalaryMax())
                .salaryRemark(jobPost.getSalaryRemark())
                .hiringCount(jobPost.getHiringCount())
                .jobDescription(jobPost.getJobDescription())
                .deadline(jobPost.getDeadline())
                .postStatus(jobPost.getPostStatus())
                .postStatusName(postStatusEnum.getDescription())
                .createTime(jobPost.getCreateTime())
                .updateTime(jobPost.getUpdateTime())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
