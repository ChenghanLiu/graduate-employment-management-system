package com.example.employment.modules.resume.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ResumeProjectCreateRequest {

    @NotBlank(message = "项目名称不能为空")
    @Size(max = 128, message = "项目名称长度不能超过128位")
    private String projectName;

    @Size(max = 128, message = "角色名称长度不能超过128位")
    private String roleName;

    private LocalDate startDate;

    private LocalDate endDate;

    @Size(max = 1000, message = "项目描述长度不能超过1000位")
    private String description;

    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortOrder = 0;

    @AssertTrue(message = "开始日期不能晚于结束日期")
    public boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }
}
