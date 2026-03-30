package com.example.employment.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.enums.EmploymentRecordReviewStatusEnum;
import com.example.employment.common.enums.StudentEmploymentStatusEnum;
import com.example.employment.entity.EmploymentRecord;
import com.example.employment.entity.EnterpriseProfile;
import com.example.employment.entity.JobApplication;
import com.example.employment.entity.JobPost;
import com.example.employment.entity.StudentProfile;
import com.example.employment.mapper.EmploymentRecordMapper;
import com.example.employment.mapper.EnterpriseProfileMapper;
import com.example.employment.mapper.JobApplicationMapper;
import com.example.employment.mapper.JobPostMapper;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.modules.admin.service.AdminDashboardService;
import com.example.employment.modules.admin.vo.AdminDashboardOverviewVO;
import org.springframework.stereotype.Service;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final StudentProfileMapper studentProfileMapper;
    private final EnterpriseProfileMapper enterpriseProfileMapper;
    private final JobPostMapper jobPostMapper;
    private final JobApplicationMapper jobApplicationMapper;
    private final EmploymentRecordMapper employmentRecordMapper;

    public AdminDashboardServiceImpl(StudentProfileMapper studentProfileMapper,
                                     EnterpriseProfileMapper enterpriseProfileMapper,
                                     JobPostMapper jobPostMapper,
                                     JobApplicationMapper jobApplicationMapper,
                                     EmploymentRecordMapper employmentRecordMapper) {
        this.studentProfileMapper = studentProfileMapper;
        this.enterpriseProfileMapper = enterpriseProfileMapper;
        this.jobPostMapper = jobPostMapper;
        this.jobApplicationMapper = jobApplicationMapper;
        this.employmentRecordMapper = employmentRecordMapper;
    }

    @Override
    public AdminDashboardOverviewVO getOverview() {
        return AdminDashboardOverviewVO.builder()
                .totalStudents(countActiveStudents())
                .totalEnterprises(countActiveEnterprises())
                .totalJobs(countActiveJobs())
                .totalApplications(countActiveApplications())
                .employedStudents(countEmployedStudents())
                .pendingEmploymentReviews(countPendingEmploymentReviews())
                .build();
    }

    private long countActiveStudents() {
        return studentProfileMapper.selectCount(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStatus, 1));
    }

    private long countActiveEnterprises() {
        return enterpriseProfileMapper.selectCount(new LambdaQueryWrapper<EnterpriseProfile>()
                .eq(EnterpriseProfile::getStatus, 1));
    }

    private long countActiveJobs() {
        return jobPostMapper.selectCount(new LambdaQueryWrapper<JobPost>()
                .eq(JobPost::getStatus, 1));
    }

    private long countActiveApplications() {
        return jobApplicationMapper.selectCount(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getStatus, 1));
    }

    private long countEmployedStudents() {
        return studentProfileMapper.selectCount(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStatus, 1)
                .eq(StudentProfile::getEmploymentStatus, StudentEmploymentStatusEnum.EMPLOYED.getCode()));
    }

    private long countPendingEmploymentReviews() {
        return employmentRecordMapper.selectCount(new LambdaQueryWrapper<EmploymentRecord>()
                .eq(EmploymentRecord::getStatus, 1)
                .eq(EmploymentRecord::getReviewStatus, EmploymentRecordReviewStatusEnum.PENDING.getCode()));
    }
}
