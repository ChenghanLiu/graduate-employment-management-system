package com.example.employment.modules.employmentattachment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.EmploymentRecordReviewStatusEnum;
import com.example.employment.entity.EmploymentAttachment;
import com.example.employment.entity.EmploymentRecord;
import com.example.employment.entity.StudentProfile;
import com.example.employment.mapper.EmploymentAttachmentMapper;
import com.example.employment.mapper.EmploymentRecordMapper;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.modules.employmentattachment.dto.EmploymentAttachmentCreateRequest;
import com.example.employment.modules.employmentattachment.service.EmploymentAttachmentService;
import com.example.employment.modules.employmentattachment.vo.EmploymentAttachmentVO;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmploymentAttachmentServiceImpl implements EmploymentAttachmentService {

    private final EmploymentAttachmentMapper employmentAttachmentMapper;
    private final EmploymentRecordMapper employmentRecordMapper;
    private final StudentProfileMapper studentProfileMapper;

    public EmploymentAttachmentServiceImpl(EmploymentAttachmentMapper employmentAttachmentMapper,
                                           EmploymentRecordMapper employmentRecordMapper,
                                           StudentProfileMapper studentProfileMapper) {
        this.employmentAttachmentMapper = employmentAttachmentMapper;
        this.employmentRecordMapper = employmentRecordMapper;
        this.studentProfileMapper = studentProfileMapper;
    }

    @Override
    public List<EmploymentAttachmentVO> listCurrentAttachments(Long studentUserId) {
        EmploymentRecord employmentRecord = getCurrentRecordOrThrow(studentUserId);
        return employmentAttachmentMapper.selectList(new LambdaQueryWrapper<EmploymentAttachment>()
                        .eq(EmploymentAttachment::getEmploymentRecordId, employmentRecord.getId())
                        .eq(EmploymentAttachment::getStatus, 1)
                        .orderByDesc(EmploymentAttachment::getId))
                .stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public EmploymentAttachmentVO addCurrentAttachment(Long studentUserId, EmploymentAttachmentCreateRequest request) {
        EmploymentRecord employmentRecord = getCurrentRecordOrThrow(studentUserId);
        if (EmploymentRecordReviewStatusEnum.APPROVED.getCode() == employmentRecord.getReviewStatus()) {
            throw new BusinessException(4090, "审核通过的就业记录不可直接维护附件");
        }

        EmploymentAttachment attachment = new EmploymentAttachment();
        attachment.setEmploymentRecordId(employmentRecord.getId());
        attachment.setFileName(request.getFileName().trim());
        attachment.setFileUrl(request.getFileUrl().trim());
        attachment.setFileType(request.getFileType().trim());
        attachment.setRemark(trimToNull(request.getRemark()));
        attachment.setStatus(1);
        employmentAttachmentMapper.insert(attachment);
        return toVO(attachment);
    }

    @Override
    public void deleteCurrentAttachment(Long studentUserId, Long attachmentId) {
        EmploymentRecord employmentRecord = getCurrentRecordOrThrow(studentUserId);
        if (EmploymentRecordReviewStatusEnum.APPROVED.getCode() == employmentRecord.getReviewStatus()) {
            throw new BusinessException(4090, "审核通过的就业记录不可直接维护附件");
        }

        EmploymentAttachment attachment = employmentAttachmentMapper.selectOne(new LambdaQueryWrapper<EmploymentAttachment>()
                .eq(EmploymentAttachment::getId, attachmentId)
                .eq(EmploymentAttachment::getEmploymentRecordId, employmentRecord.getId())
                .eq(EmploymentAttachment::getStatus, 1)
                .last("limit 1"));
        if (attachment == null) {
            throw new BusinessException(4040, "就业附件不存在");
        }
        attachment.setStatus(0);
        employmentAttachmentMapper.updateById(attachment);
    }

    private EmploymentRecord getCurrentRecordOrThrow(Long studentUserId) {
        StudentProfile studentProfile = studentProfileMapper.selectOne(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getUserId, studentUserId)
                .eq(StudentProfile::getStatus, 1)
                .last("limit 1"));
        if (studentProfile == null) {
            throw new BusinessException(4040, "学生档案不存在，请先完善学生档案");
        }
        EmploymentRecord employmentRecord = employmentRecordMapper.selectOne(new LambdaQueryWrapper<EmploymentRecord>()
                .eq(EmploymentRecord::getStudentId, studentProfile.getId())
                .eq(EmploymentRecord::getStatus, 1)
                .last("limit 1"));
        if (employmentRecord == null) {
            throw new BusinessException(4040, "当前就业记录不存在");
        }
        return employmentRecord;
    }

    private EmploymentAttachmentVO toVO(EmploymentAttachment attachment) {
        return EmploymentAttachmentVO.builder()
                .id(attachment.getId())
                .employmentRecordId(attachment.getEmploymentRecordId())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .fileType(attachment.getFileType())
                .remark(attachment.getRemark())
                .createTime(attachment.getCreateTime())
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
