package com.example.employment.modules.jobapplication.service;

import com.example.employment.modules.jobapplication.dto.JobApplicationCreateRequest;
import com.example.employment.modules.jobapplication.dto.JobApplicationStatusUpdateRequest;
import com.example.employment.modules.jobapplication.vo.JobApplicationDetailVO;
import java.util.List;

public interface JobApplicationService {

    JobApplicationDetailVO createApplication(Long studentUserId, JobApplicationCreateRequest request);

    List<JobApplicationDetailVO> listMyApplications(Long studentUserId);

    List<JobApplicationDetailVO> listReceivedApplications(Long enterpriseUserId);

    JobApplicationDetailVO updateApplicationStatus(Long enterpriseUserId, Long applicationId,
                                                   JobApplicationStatusUpdateRequest request);
}
