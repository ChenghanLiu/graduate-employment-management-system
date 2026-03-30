package com.example.employment.modules.resume.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumeDetailVO {

    private final Long id;

    private final Long studentId;

    private final String resumeName;

    private final String jobIntention;

    private final String expectedCity;

    private final String expectedSalary;

    private final String selfEvaluation;

    private final Integer completionRate;

    private final Integer isDefault;
}
