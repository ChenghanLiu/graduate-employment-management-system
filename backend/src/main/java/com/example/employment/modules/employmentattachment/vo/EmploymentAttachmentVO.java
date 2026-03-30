package com.example.employment.modules.employmentattachment.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmploymentAttachmentVO {

    private final Long id;

    private final Long employmentRecordId;

    private final String fileName;

    private final String fileUrl;

    private final String fileType;

    private final String remark;

    private final LocalDateTime createTime;
}
