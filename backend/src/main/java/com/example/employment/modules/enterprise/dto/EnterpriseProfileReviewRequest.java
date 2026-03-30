package com.example.employment.modules.enterprise.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EnterpriseProfileReviewRequest {

    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态不合法")
    @Max(value = 2, message = "审核状态不合法")
    private Integer reviewStatus;

    @Size(max = 255, message = "审核备注长度不能超过255位")
    private String reviewRemark;
}
