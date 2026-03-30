package com.example.employment.modules.statistics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.enums.JobApplicationStatusEnum;
import com.example.employment.common.enums.StudentEmploymentStatusEnum;
import com.example.employment.entity.JobApplication;
import com.example.employment.entity.JobPost;
import com.example.employment.entity.StudentProfile;
import com.example.employment.mapper.JobApplicationMapper;
import com.example.employment.mapper.JobPostMapper;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.modules.statistics.service.StatisticsService;
import com.example.employment.modules.statistics.vo.EmploymentStatusStatisticsVO;
import com.example.employment.modules.statistics.vo.JobStatisticsVO;
import com.example.employment.modules.statistics.vo.StatisticsOverviewVO;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StudentProfileMapper studentProfileMapper;
    private final JobPostMapper jobPostMapper;
    private final JobApplicationMapper jobApplicationMapper;

    public StatisticsServiceImpl(StudentProfileMapper studentProfileMapper,
                                 JobPostMapper jobPostMapper,
                                 JobApplicationMapper jobApplicationMapper) {
        this.studentProfileMapper = studentProfileMapper;
        this.jobPostMapper = jobPostMapper;
        this.jobApplicationMapper = jobApplicationMapper;
    }

    @Override
    public StatisticsOverviewVO getOverview() {
        long totalStudents = countActiveStudents();
        long employedStudents = studentProfileMapper.selectCount(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStatus, 1)
                .eq(StudentProfile::getEmploymentStatus, StudentEmploymentStatusEnum.EMPLOYED.getCode()));
        long unemployedStudents = Math.max(totalStudents - employedStudents, 0);
        BigDecimal employmentRate = totalStudents == 0
                ? BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(employedStudents)
                .divide(BigDecimal.valueOf(totalStudents), 4, RoundingMode.HALF_UP);
        return StatisticsOverviewVO.builder()
                .totalStudents(totalStudents)
                .employedStudents(employedStudents)
                .unemployedStudents(unemployedStudents)
                .employmentRate(employmentRate)
                .build();
    }

    @Override
    public List<EmploymentStatusStatisticsVO> getEmploymentStatusStatistics() {
        Map<Integer, Long> countMap = studentProfileMapper.selectList(new LambdaQueryWrapper<StudentProfile>()
                        .select(StudentProfile::getEmploymentStatus)
                        .eq(StudentProfile::getStatus, 1))
                .stream()
                .collect(Collectors.groupingBy(StudentProfile::getEmploymentStatus, Collectors.counting()));

        return Arrays.stream(StudentEmploymentStatusEnum.values())
                .map(item -> EmploymentStatusStatisticsVO.builder()
                        .employmentStatus(item.getCode())
                        .employmentStatusName(item.getDescription())
                        .studentCount(countMap.getOrDefault(item.getCode(), 0L))
                        .build())
                .toList();
    }

    @Override
    public JobStatisticsVO getJobStatistics() {
        long totalJobs = jobPostMapper.selectCount(new LambdaQueryWrapper<JobPost>()
                .eq(JobPost::getStatus, 1));
        long totalApplications = jobApplicationMapper.selectCount(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getStatus, 1));
        long hiredCount = jobApplicationMapper.selectCount(new LambdaQueryWrapper<JobApplication>()
                .eq(JobApplication::getStatus, 1)
                .eq(JobApplication::getApplicationStatus, JobApplicationStatusEnum.HIRED.getCode()));
        return JobStatisticsVO.builder()
                .totalJobs(totalJobs)
                .totalApplications(totalApplications)
                .hiredCount(hiredCount)
                .build();
    }

    private long countActiveStudents() {
        return studentProfileMapper.selectCount(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStatus, 1));
    }
}
