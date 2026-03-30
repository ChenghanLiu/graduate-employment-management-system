package com.example.employment.modules.resume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeCreateRequest {

    @NotBlank(message = "简历名称不能为空")
    @Size(max = 128, message = "简历名称长度不能超过128位")
    private String resumeName;

    @Size(max = 255, message = "求职意向长度不能超过255位")
    private String jobIntention;

    @Size(max = 128, message = "期望城市长度不能超过128位")
    private String expectedCity;

    @Size(max = 64, message = "期望薪资长度不能超过64位")
    private String expectedSalary;

    @Size(max = 1000, message = "自我评价长度不能超过1000位")
    private String selfEvaluation;
}
