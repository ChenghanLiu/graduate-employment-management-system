package com.example.employment.modules.jobapplication.vo;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JobApplicationDetailVO {

    private final Long id;

    private final Long jobPostId;

    private final String jobTitle;

    private final Long enterpriseId;

    private final String enterpriseName;

    private final Long studentId;

    private final String studentNo;

    private final String collegeName;

    private final String majorName;

    private final Long resumeId;

    private final String resumeName;

    private final Integer applicationStatus;

    private final String applicationStatusName;

    private final LocalDateTime applyTime;

    private final LocalDateTime processedTime;

    private final String remark;
}
