package com.example.employment.modules.resume.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeSkillCreateRequest {

    @NotBlank(message = "技能名称不能为空")
    @Size(max = 128, message = "技能名称长度不能超过128位")
    private String skillName;

    @Min(value = 1, message = "熟练度不合法")
    @Max(value = 4, message = "熟练度不合法")
    private Integer skillLevel;

    @Size(max = 500, message = "技能说明长度不能超过500位")
    private String description;

    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortOrder = 0;
}
