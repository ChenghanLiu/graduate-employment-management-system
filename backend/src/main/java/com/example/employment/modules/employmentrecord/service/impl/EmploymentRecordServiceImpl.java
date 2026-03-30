package com.example.employment.modules.employmentrecord.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.EmploymentRecordReviewStatusEnum;
import com.example.employment.common.enums.StudentEmploymentStatusEnum;
import com.example.employment.entity.EmploymentRecord;
import com.example.employment.entity.StudentProfile;
import com.example.employment.mapper.EmploymentRecordMapper;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordCreateRequest;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordReviewRequest;
import com.example.employment.modules.employmentrecord.dto.EmploymentRecordUpdateRequest;
import com.example.employment.modules.employmentrecord.service.EmploymentRecordService;
import com.example.employment.modules.employmentrecord.vo.EmploymentRecordDetailVO;
import com.example.employment.modules.employmentrecord.vo.EmploymentRecordReviewVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmploymentRecordServiceImpl implements EmploymentRecordService {

    private final EmploymentRecordMapper employmentRecordMapper;
    private final StudentProfileMapper studentProfileMapper;

    public EmploymentRecordServiceImpl(EmploymentRecordMapper employmentRecordMapper,
                                       StudentProfileMapper studentProfileMapper) {
        this.employmentRecordMapper = employmentRecordMapper;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Override
    public EmploymentRecordDetailVO getCurrentRecord(Long studentUserId) {
        StudentProfile studentProfile = getStudentProfileOrThrow(studentUserId);
        EmploymentRecord employmentRecord = getCurrentRecordByStudentId(studentProfile.getId());
        if (employmentRecord == null) {
            throw new BusinessException(4040, "当前就业记录不存在");
        }
        return toDetailVO(employmentRecord, studentProfile);
    }

    @Override
    public EmploymentRecordDetailVO createCurrentRecord(Long studentUserId, EmploymentRecordCreateRequest request) {
        StudentProfile studentProfile = getStudentProfileOrThrow(studentUserId);
        if (getCurrentRecordByStudentId(studentProfile.getId()) != null) {
            throw new BusinessException(4090, "当前学生已存在就业记录");
        }
        validateEmploymentStatus(request.getEmploymentStatus());

        EmploymentRecord employmentRecord = new EmploymentRecord();
        employmentRecord.setStudentId(studentProfile.getId());
        fillRecord(employmentRecord, request);
        employmentRecord.setReviewStatus(EmploymentRecordReviewStatusEnum.PENDING.getCode());
        employmentRecord.setStatus(1);
        employmentRecordMapper.insert(employmentRecord);
        return toDetailVO(employmentRecord, studentProfile);
    }

    @Override
    public EmploymentRecordDetailVO updateCurrentRecord(Long studentUserId, EmploymentRecordUpdateRequest request) {
        StudentProfile studentProfile = getStudentProfileOrThrow(studentUserId);
        EmploymentRecord employmentRecord = getCurrentRecordByStudentId(studentProfile.getId());
        if (employmentRecord == null) {
            throw new BusinessException(4040, "当前就业记录不存在");
        }
        EmploymentRecordReviewStatusEnum currentStatus =
                EmploymentRecordReviewStatusEnum.fromCode(employmentRecord.getReviewStatus());
        if (EmploymentRecordReviewStatusEnum.APPROVED == currentStatus) {
            throw new BusinessException(4090, "审核通过的就业记录不可直接修改");
        }
        validateEmploymentStatus(request.getEmploymentStatus());

        fillRecord(employmentRecord, request);
        employmentRecord.setReviewStatus(EmploymentRecordReviewStatusEnum.PENDING.getCode());
        employmentRecord.setReviewRemark(null);
        employmentRecord.setReviewerUserId(null);
        employmentRecord.setReviewedTime(null);
        employmentRecordMapper.updateById(employmentRecord);
        return toDetailVO(employmentRecord, studentProfile);
    }

    @Override
    public List<EmploymentRecordReviewVO> listReviewRecords(Long reviewerUserId) {
        List<EmploymentRecord> employmentRecords = employmentRecordMapper.selectList(new LambdaQueryWrapper<EmploymentRecord>()
                .eq(EmploymentRecord::getStatus, 1)
                .orderByAsc(EmploymentRecord::getReviewStatus)
                .orderByDesc(EmploymentRecord::getUpdateTime)
                .orderByDesc(EmploymentRecord::getId));
        return employmentRecords.stream()
                .map(record -> toReviewVO(record, getStudentProfileByIdOrThrow(record.getStudentId())))
                .toList();
    }

    @Override
    public EmploymentRecordDetailVO getRecordDetail(Long reviewerUserId, Long recordId) {
        EmploymentRecord employmentRecord = getRecordByIdOrThrow(recordId);
        StudentProfile studentProfile = getStudentProfileByIdOrThrow(employmentRecord.getStudentId());
        return toDetailVO(employmentRecord, studentProfile);
    }

    @Override
    public EmploymentRecordDetailVO reviewRecord(Long reviewerUserId, Long recordId, EmploymentRecordReviewRequest request) {
        EmploymentRecord employmentRecord = getRecordByIdOrThrow(recordId);
        StudentProfile studentProfile = getStudentProfileByIdOrThrow(employmentRecord.getStudentId());
        EmploymentRecordReviewStatusEnum currentStatus =
                EmploymentRecordReviewStatusEnum.fromCode(employmentRecord.getReviewStatus());
        if (EmploymentRecordReviewStatusEnum.PENDING != currentStatus) {
            throw new BusinessException(4090, "当前就业记录状态不允许审核");
        }

        EmploymentRecordReviewStatusEnum nextStatus = EmploymentRecordReviewStatusEnum.fromCode(request.getReviewStatus());
        if (EmploymentRecordReviewStatusEnum.PENDING == nextStatus) {
            throw new BusinessException(4000, "审核操作不允许回写为待审核");
        }
        if (EmploymentRecordReviewStatusEnum.REJECTED == nextStatus
                && !StringUtils.hasText(request.getReviewRemark())) {
            throw new BusinessException(4000, "驳回时必须填写审核备注");
        }

        employmentRecord.setReviewStatus(nextStatus.getCode());
        employmentRecord.setReviewRemark(trimToNull(request.getReviewRemark()));
        employmentRecord.setReviewerUserId(reviewerUserId);
        employmentRecord.setReviewedTime(LocalDateTime.now());
        employmentRecordMapper.updateById(employmentRecord);

        if (EmploymentRecordReviewStatusEnum.APPROVED == nextStatus) {
            studentProfile.setEmploymentStatus(employmentRecord.getEmploymentStatus());
            studentProfileMapper.updateById(studentProfile);
        }

        return toDetailVO(employmentRecord, studentProfile);
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

    private EmploymentRecord getCurrentRecordByStudentId(Long studentId) {
        return employmentRecordMapper.selectOne(new LambdaQueryWrapper<EmploymentRecord>()
                .eq(EmploymentRecord::getStudentId, studentId)
                .eq(EmploymentRecord::getStatus, 1)
                .last("limit 1"));
    }

    private EmploymentRecord getRecordByIdOrThrow(Long recordId) {
        EmploymentRecord employmentRecord = employmentRecordMapper.selectOne(new LambdaQueryWrapper<EmploymentRecord>()
                .eq(EmploymentRecord::getId, recordId)
                .eq(EmploymentRecord::getStatus, 1)
                .last("limit 1"));
        if (employmentRecord == null) {
            throw new BusinessException(4040, "就业记录不存在");
        }
        return employmentRecord;
    }

    private void fillRecord(EmploymentRecord employmentRecord, EmploymentRecordCreateRequest request) {
        employmentRecord.setJobPostId(request.getJobPostId());
        employmentRecord.setEnterpriseId(request.getEnterpriseId());
        employmentRecord.setEmploymentStatus(request.getEmploymentStatus());
        employmentRecord.setEmploymentType(request.getEmploymentType().trim());
        employmentRecord.setCompanyName(request.getCompanyName().trim());
        employmentRecord.setJobTitle(request.getJobTitle().trim());
        employmentRecord.setWorkCity(trimToNull(request.getWorkCity()));
        employmentRecord.setContractStartDate(request.getContractStartDate());
        employmentRecord.setSalaryAmount(request.getSalaryAmount());
        employmentRecord.setReportSource(trimToNull(request.getReportSource()));
    }

    private void validateEmploymentStatus(Integer employmentStatus) {
        StudentEmploymentStatusEnum.fromCode(employmentStatus);
    }

    private EmploymentRecordDetailVO toDetailVO(EmploymentRecord employmentRecord, StudentProfile studentProfile) {
        StudentEmploymentStatusEnum employmentStatusEnum =
                StudentEmploymentStatusEnum.fromCode(employmentRecord.getEmploymentStatus());
        EmploymentRecordReviewStatusEnum reviewStatusEnum =
                EmploymentRecordReviewStatusEnum.fromCode(employmentRecord.getReviewStatus());
        return EmploymentRecordDetailVO.builder()
                .id(employmentRecord.getId())
                .studentId(employmentRecord.getStudentId())
                .studentNo(studentProfile.getStudentNo())
                .jobPostId(employmentRecord.getJobPostId())
                .enterpriseId(employmentRecord.getEnterpriseId())
                .companyName(employmentRecord.getCompanyName())
                .employmentType(employmentRecord.getEmploymentType())
                .employmentStatus(employmentRecord.getEmploymentStatus())
                .employmentStatusName(employmentStatusEnum.getDescription())
                .jobTitle(employmentRecord.getJobTitle())
                .workCity(employmentRecord.getWorkCity())
                .contractStartDate(employmentRecord.getContractStartDate())
                .salaryAmount(employmentRecord.getSalaryAmount())
                .reportSource(employmentRecord.getReportSource())
                .reviewStatus(employmentRecord.getReviewStatus())
                .reviewStatusName(reviewStatusEnum.getDescription())
                .reviewRemark(employmentRecord.getReviewRemark())
                .reviewerUserId(employmentRecord.getReviewerUserId())
                .reviewedTime(employmentRecord.getReviewedTime())
                .createTime(employmentRecord.getCreateTime())
                .updateTime(employmentRecord.getUpdateTime())
                .build();
    }

    private EmploymentRecordReviewVO toReviewVO(EmploymentRecord employmentRecord, StudentProfile studentProfile) {
        StudentEmploymentStatusEnum employmentStatusEnum =
                StudentEmploymentStatusEnum.fromCode(employmentRecord.getEmploymentStatus());
        EmploymentRecordReviewStatusEnum reviewStatusEnum =
                EmploymentRecordReviewStatusEnum.fromCode(employmentRecord.getReviewStatus());
        return EmploymentRecordReviewVO.builder()
                .id(employmentRecord.getId())
                .studentId(employmentRecord.getStudentId())
                .studentNo(studentProfile.getStudentNo())
                .companyName(employmentRecord.getCompanyName())
                .jobTitle(employmentRecord.getJobTitle())
                .employmentStatus(employmentRecord.getEmploymentStatus())
                .employmentStatusName(employmentStatusEnum.getDescription())
                .reviewStatus(employmentRecord.getReviewStatus())
                .reviewStatusName(reviewStatusEnum.getDescription())
                .reviewRemark(employmentRecord.getReviewRemark())
                .updateTime(employmentRecord.getUpdateTime())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
