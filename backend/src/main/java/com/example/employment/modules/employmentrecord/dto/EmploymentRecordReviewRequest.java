package com.example.employment.modules.employmentrecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmploymentRecordReviewRequest {

    @NotNull(message = "审核状态不能为空")
    private Integer reviewStatus;

    @Size(max = 255, message = "审核备注长度不能超过255位")
    private String reviewRemark;
}
