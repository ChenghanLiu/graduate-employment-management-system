package com.example.employment.modules.resume.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ResumeEducationCreateRequest {

    @NotBlank(message = "学校名称不能为空")
    @Size(max = 128, message = "学校名称长度不能超过128位")
    private String schoolName;

    @NotBlank(message = "专业名称不能为空")
    @Size(max = 128, message = "专业名称长度不能超过128位")
    private String majorName;

    @NotBlank(message = "学历层次不能为空")
    @Size(max = 64, message = "学历层次长度不能超过64位")
    private String educationLevel;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 1000, message = "经历描述长度不能超过1000位")
    private String description;

    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortOrder = 0;

    @AssertTrue(message = "开始日期不能晚于结束日期")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }
}
