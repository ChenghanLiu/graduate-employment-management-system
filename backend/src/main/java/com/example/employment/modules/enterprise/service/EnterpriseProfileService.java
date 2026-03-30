package com.example.employment.modules.enterprise.service;

import com.example.employment.modules.enterprise.dto.EnterpriseProfileCreateRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileReviewRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileUpdateRequest;
import com.example.employment.modules.enterprise.vo.EnterpriseProfileDetailVO;
import com.example.employment.modules.enterprise.vo.EnterpriseReviewStatusVO;
import java.util.List;

public interface EnterpriseProfileService {

    EnterpriseProfileDetailVO getCurrentProfile(Long userId);

    EnterpriseProfileDetailVO createProfile(Long userId, EnterpriseProfileCreateRequest request);

    EnterpriseProfileDetailVO updateProfile(Long userId, EnterpriseProfileUpdateRequest request);

    EnterpriseReviewStatusVO getCurrentReviewStatus(Long userId);

    EnterpriseProfileDetailVO reviewProfile(Long profileId, EnterpriseProfileReviewRequest request);

    List<EnterpriseProfileDetailVO> listAdminProfiles();

    EnterpriseProfileDetailVO getAdminProfile(Long profileId);

    EnterpriseProfileDetailVO updateAdminProfile(Long profileId, EnterpriseProfileUpdateRequest request);

    void disableAdminProfile(Long profileId);
}
