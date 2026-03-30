package com.example.employment.modules.jobpost.service;

import com.example.employment.modules.jobpost.dto.JobPostCreateRequest;
import com.example.employment.modules.jobpost.dto.JobPostStatusUpdateRequest;
import com.example.employment.modules.jobpost.dto.JobPostUpdateRequest;
import com.example.employment.modules.jobpost.vo.JobPostDetailVO;
import java.util.List;

public interface JobPostService {

    List<JobPostDetailVO> listMyJobPosts(Long enterpriseUserId);

    JobPostDetailVO createJobPost(Long enterpriseUserId, JobPostCreateRequest request);

    JobPostDetailVO updateJobPost(Long enterpriseUserId, Long jobPostId, JobPostUpdateRequest request);

    JobPostDetailVO updateJobPostStatus(Long enterpriseUserId, Long jobPostId, JobPostStatusUpdateRequest request);

    List<JobPostDetailVO> listOpenJobPosts();

    JobPostDetailVO getOpenJobPostDetail(Long jobPostId);
}
