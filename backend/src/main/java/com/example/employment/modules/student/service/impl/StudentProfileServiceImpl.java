package com.example.employment.modules.student.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.ApiCode;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.StudentEmploymentStatusEnum;
import com.example.employment.entity.StudentProfile;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.modules.student.dto.StudentProfileCreateRequest;
import com.example.employment.modules.student.dto.StudentProfileUpdateRequest;
import com.example.employment.modules.student.service.StudentProfileService;
import com.example.employment.modules.student.vo.StudentProfileDetailVO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StudentProfileServiceImpl implements StudentProfileService {

    private final StudentProfileMapper studentProfileMapper;

    public StudentProfileServiceImpl(StudentProfileMapper studentProfileMapper) {
        this.studentProfileMapper = studentProfileMapper;
    }

    @Override
    public StudentProfileDetailVO getCurrentProfile(Long userId) {
        StudentProfile profile = getByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "学生档案不存在");
        }
        return toDetailVO(profile);
    }

    @Override
    public StudentProfileDetailVO createProfile(Long userId, StudentProfileCreateRequest request) {
        StudentProfile existingProfile = getByUserId(userId);
        if (existingProfile != null) {
            return toDetailVO(existingProfile);
        }
        validateEmploymentStatus(request.getEmploymentStatus());
        ensureStudentNoUnique(request.getStudentNo(), null);

        StudentProfile profile = new StudentProfile();
        profile.setUserId(userId);
        fillProfile(profile, request);
        profile.setStatus(1);
        studentProfileMapper.insert(profile);
        return toDetailVO(profile);
    }

    @Override
    public StudentProfileDetailVO updateProfile(Long userId, StudentProfileUpdateRequest request) {
        StudentProfile profile = getByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "学生档案不存在");
        }
        validateEmploymentStatus(request.getEmploymentStatus());
        ensureStudentNoUnique(request.getStudentNo(), profile.getId());

        fillProfile(profile, request);
        studentProfileMapper.updateById(profile);
        return toDetailVO(profile);
    }

    @Override
    public List<StudentProfileDetailVO> listAdminProfiles() {
        return studentProfileMapper.selectList(new LambdaQueryWrapper<StudentProfile>()
                        .orderByDesc(StudentProfile::getCreateTime)
                        .orderByDesc(StudentProfile::getId))
                .stream()
                .map(this::toDetailVO)
                .toList();
    }

    @Override
    public StudentProfileDetailVO getAdminProfile(Long profileId) {
        return toDetailVO(getByIdOrThrow(profileId));
    }

    @Override
    public StudentProfileDetailVO updateAdminProfile(Long profileId, StudentProfileUpdateRequest request) {
        StudentProfile profile = getByIdOrThrow(profileId);
        validateEmploymentStatus(request.getEmploymentStatus());
        ensureStudentNoUnique(request.getStudentNo(), profileId);
        fillProfile(profile, request);
        studentProfileMapper.updateById(profile);
        return toDetailVO(profile);
    }

    @Override
    public void disableAdminProfile(Long profileId) {
        StudentProfile profile = getByIdOrThrow(profileId);
        profile.setStatus(0);
        studentProfileMapper.updateById(profile);
    }

    private StudentProfile getByUserId(Long userId) {
        return studentProfileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getUserId, userId)
                .last("limit 1"));
    }

    private void ensureStudentNoUnique(String studentNo, Long excludeId) {
        StudentProfile existing = studentProfileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentNo, studentNo)
                .ne(excludeId != null, StudentProfile::getId, excludeId)
                .last("limit 1"));
        if (existing != null) {
            throw new BusinessException(ApiCode.CONFLICT, "学号已存在");
        }
    }

    private void fillProfile(StudentProfile profile, StudentProfileCreateRequest request) {
        profile.setStudentNo(request.getStudentNo().trim());
        profile.setGender(request.getGender());
        profile.setCollegeName(request.getCollegeName().trim());
        profile.setMajorName(request.getMajorName().trim());
        profile.setClassName(trimToNull(request.getClassName()));
        profile.setEducationLevel(request.getEducationLevel().trim());
        profile.setGraduationYear(request.getGraduationYear());
        profile.setNativePlace(trimToNull(request.getNativePlace()));
        profile.setEmploymentStatus(request.getEmploymentStatus());
    }

    private void validateEmploymentStatus(Integer employmentStatus) {
        StudentEmploymentStatusEnum.fromCode(employmentStatus);
    }

    private StudentProfileDetailVO toDetailVO(StudentProfile profile) {
        StudentEmploymentStatusEnum statusEnum = StudentEmploymentStatusEnum.fromCode(profile.getEmploymentStatus());
        return StudentProfileDetailVO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .studentNo(profile.getStudentNo())
                .gender(profile.getGender())
                .collegeName(profile.getCollegeName())
                .majorName(profile.getMajorName())
                .className(profile.getClassName())
                .educationLevel(profile.getEducationLevel())
                .graduationYear(profile.getGraduationYear())
                .nativePlace(profile.getNativePlace())
                .employmentStatus(profile.getEmploymentStatus())
                .employmentStatusName(statusEnum.getDescription())
                .status(profile.getStatus())
                .createTime(profile.getCreateTime())
                .updateTime(profile.getUpdateTime())
                .build();
    }

    private StudentProfile getByIdOrThrow(Long profileId) {
        StudentProfile profile = studentProfileMapper.selectById(profileId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "学生档案不存在");
        }
        return profile;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
