package com.example.employment.modules.resume.vo;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumeEducationVO {

    private final Long id;

    private final Long resumeId;

    private final String schoolName;

    private final String majorName;

    private final String educationLevel;

    private final LocalDate startDate;

    private final LocalDate endDate;

    private final String description;

    private final Integer sortOrder;
}
