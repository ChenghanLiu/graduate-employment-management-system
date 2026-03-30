package com.example.employment.modules.student.service;

import com.example.employment.modules.student.dto.StudentProfileCreateRequest;
import com.example.employment.modules.student.dto.StudentProfileUpdateRequest;
import com.example.employment.modules.student.vo.StudentProfileDetailVO;
import java.util.List;

public interface StudentProfileService {

    StudentProfileDetailVO getCurrentProfile(Long userId);

    StudentProfileDetailVO createProfile(Long userId, StudentProfileCreateRequest request);

    StudentProfileDetailVO updateProfile(Long userId, StudentProfileUpdateRequest request);

    List<StudentProfileDetailVO> listAdminProfiles();

    StudentProfileDetailVO getAdminProfile(Long profileId);

    StudentProfileDetailVO updateAdminProfile(Long profileId, StudentProfileUpdateRequest request);

    void disableAdminProfile(Long profileId);
}
