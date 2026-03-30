package com.example.employment.modules.resume.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumeSkillVO {

    private final Long id;

    private final Long resumeId;

    private final String skillName;

    private final Integer skillLevel;

    private final String description;

    private final Integer sortOrder;
}
