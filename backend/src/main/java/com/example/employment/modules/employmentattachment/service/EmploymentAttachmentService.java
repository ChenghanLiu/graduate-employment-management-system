package com.example.employment.modules.employmentattachment.service;

import com.example.employment.modules.employmentattachment.dto.EmploymentAttachmentCreateRequest;
import com.example.employment.modules.employmentattachment.vo.EmploymentAttachmentVO;
import java.util.List;

public interface EmploymentAttachmentService {

    List<EmploymentAttachmentVO> listCurrentAttachments(Long studentUserId);

    EmploymentAttachmentVO addCurrentAttachment(Long studentUserId, EmploymentAttachmentCreateRequest request);

    void deleteCurrentAttachment(Long studentUserId, Long attachmentId);
}
