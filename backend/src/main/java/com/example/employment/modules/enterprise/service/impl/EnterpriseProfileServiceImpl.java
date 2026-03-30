package com.example.employment.modules.enterprise.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.ApiCode;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.EnterpriseReviewStatusEnum;
import com.example.employment.entity.EnterpriseProfile;
import com.example.employment.mapper.EnterpriseProfileMapper;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileCreateRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileReviewRequest;
import com.example.employment.modules.enterprise.dto.EnterpriseProfileUpdateRequest;
import com.example.employment.modules.enterprise.service.EnterpriseProfileService;
import com.example.employment.modules.enterprise.vo.EnterpriseProfileDetailVO;
import com.example.employment.modules.enterprise.vo.EnterpriseReviewStatusVO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EnterpriseProfileServiceImpl implements EnterpriseProfileService {

    private final EnterpriseProfileMapper enterpriseProfileMapper;

    public EnterpriseProfileServiceImpl(EnterpriseProfileMapper enterpriseProfileMapper) {
        this.enterpriseProfileMapper = enterpriseProfileMapper;
    }

    @Override
    public EnterpriseProfileDetailVO getCurrentProfile(Long userId) {
        EnterpriseProfile profile = getByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "企业档案不存在");
        }
        return toDetailVO(profile);
    }

    @Override
    public EnterpriseProfileDetailVO createProfile(Long userId, EnterpriseProfileCreateRequest request) {
        if (getByUserId(userId) != null) {
            throw new BusinessException(ApiCode.CONFLICT, "当前用户已初始化企业档案");
        }
        ensureCreditCodeUnique(request.getCreditCode(), null);

        EnterpriseProfile profile = new EnterpriseProfile();
        profile.setUserId(userId);
        fillProfile(profile, request);
        profile.setReviewStatus(EnterpriseReviewStatusEnum.PENDING.getCode());
        profile.setStatus(1);
        enterpriseProfileMapper.insert(profile);
        return toDetailVO(profile);
    }

    @Override
    public EnterpriseProfileDetailVO updateProfile(Long userId, EnterpriseProfileUpdateRequest request) {
        EnterpriseProfile profile = getByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "企业档案不存在");
        }
        ensureCreditCodeUnique(request.getCreditCode(), profile.getId());
        fillProfile(profile, request);
        profile.setReviewStatus(EnterpriseReviewStatusEnum.PENDING.getCode());
        profile.setReviewRemark(null);
        enterpriseProfileMapper.updateById(profile);
        return toDetailVO(profile);
    }

    @Override
    public EnterpriseReviewStatusVO getCurrentReviewStatus(Long userId) {
        EnterpriseProfile profile = getByUserId(userId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "企业档案不存在");
        }
        EnterpriseReviewStatusEnum reviewStatusEnum = EnterpriseReviewStatusEnum.fromCode(profile.getReviewStatus());
        return new EnterpriseReviewStatusVO(
                profile.getReviewStatus(),
                reviewStatusEnum.getDescription(),
                profile.getReviewRemark()
        );
    }

    @Override
    public EnterpriseProfileDetailVO reviewProfile(Long profileId, EnterpriseProfileReviewRequest request) {
        EnterpriseProfile profile = enterpriseProfileMapper.selectById(profileId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "企业档案不存在");
        }
        EnterpriseReviewStatusEnum reviewStatusEnum = EnterpriseReviewStatusEnum.fromCode(request.getReviewStatus());
        if (EnterpriseReviewStatusEnum.PENDING == reviewStatusEnum) {
            throw new BusinessException("审核操作不允许回写为待审核");
        }
        profile.setReviewStatus(reviewStatusEnum.getCode());
        profile.setReviewRemark(trimToNull(request.getReviewRemark()));
        enterpriseProfileMapper.updateById(profile);
        return toDetailVO(profile);
    }

    @Override
    public List<EnterpriseProfileDetailVO> listAdminProfiles() {
        return enterpriseProfileMapper.selectList(new LambdaQueryWrapper<EnterpriseProfile>()
                        .orderByDesc(EnterpriseProfile::getCreateTime)
                        .orderByDesc(EnterpriseProfile::getId))
                .stream()
                .map(this::toDetailVO)
                .toList();
    }

    @Override
    public EnterpriseProfileDetailVO getAdminProfile(Long profileId) {
        return toDetailVO(getProfileByIdOrThrow(profileId));
    }

    @Override
    public EnterpriseProfileDetailVO updateAdminProfile(Long profileId, EnterpriseProfileUpdateRequest request) {
        EnterpriseProfile profile = getProfileByIdOrThrow(profileId);
        ensureCreditCodeUnique(request.getCreditCode(), profileId);
        fillProfile(profile, request);
        enterpriseProfileMapper.updateById(profile);
        return toDetailVO(profile);
    }

    @Override
    public void disableAdminProfile(Long profileId) {
        EnterpriseProfile profile = getProfileByIdOrThrow(profileId);
        profile.setStatus(0);
        enterpriseProfileMapper.updateById(profile);
    }

    private EnterpriseProfile getByUserId(Long userId) {
        return enterpriseProfileMapper.selectOne(new LambdaQueryWrapper<EnterpriseProfile>()
                .eq(EnterpriseProfile::getUserId, userId)
                .last("limit 1"));
    }

    private void ensureCreditCodeUnique(String creditCode, Long excludeId) {
        EnterpriseProfile existing = enterpriseProfileMapper.selectOne(new LambdaQueryWrapper<EnterpriseProfile>()
                .eq(EnterpriseProfile::getCreditCode, creditCode)
                .ne(excludeId != null, EnterpriseProfile::getId, excludeId)
                .last("limit 1"));
        if (existing != null) {
            throw new BusinessException(ApiCode.CONFLICT, "统一社会信用代码已存在");
        }
    }

    private void fillProfile(EnterpriseProfile profile, EnterpriseProfileCreateRequest request) {
        profile.setEnterpriseName(request.getEnterpriseName().trim());
        profile.setCreditCode(request.getCreditCode().trim());
        profile.setIndustryName(trimToNull(request.getIndustryName()));
        profile.setEnterpriseScale(trimToNull(request.getEnterpriseScale()));
        profile.setContactName(request.getContactName().trim());
        profile.setContactPhone(request.getContactPhone().trim());
        profile.setContactEmail(trimToNull(request.getContactEmail()));
        profile.setAddress(trimToNull(request.getAddress()));
        profile.setIntroduction(trimToNull(request.getIntroduction()));
    }

    private EnterpriseProfileDetailVO toDetailVO(EnterpriseProfile profile) {
        EnterpriseReviewStatusEnum reviewStatusEnum = EnterpriseReviewStatusEnum.fromCode(profile.getReviewStatus());
        return EnterpriseProfileDetailVO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .enterpriseName(profile.getEnterpriseName())
                .creditCode(profile.getCreditCode())
                .industryName(profile.getIndustryName())
                .enterpriseScale(profile.getEnterpriseScale())
                .contactName(profile.getContactName())
                .contactPhone(profile.getContactPhone())
                .contactEmail(profile.getContactEmail())
                .address(profile.getAddress())
                .introduction(profile.getIntroduction())
                .reviewStatus(profile.getReviewStatus())
                .reviewStatusName(reviewStatusEnum.getDescription())
                .reviewRemark(profile.getReviewRemark())
                .status(profile.getStatus())
                .createTime(profile.getCreateTime())
                .updateTime(profile.getUpdateTime())
                .build();
    }

    private EnterpriseProfile getProfileByIdOrThrow(Long profileId) {
        EnterpriseProfile profile = enterpriseProfileMapper.selectById(profileId);
        if (profile == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "企业档案不存在");
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
