package com.example.employment.modules.resume.vo;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumeProjectVO {

    private final Long id;

    private final Long resumeId;

    private final String projectName;

    private final String roleName;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final String description;

    private final Integer sortOrder;
}
