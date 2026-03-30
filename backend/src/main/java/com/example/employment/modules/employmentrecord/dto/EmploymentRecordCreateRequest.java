package com.example.employment.modules.employmentrecord.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class EmploymentRecordCreateRequest {

    @NotNull(message = "就业状态不能为空")
    @Min(value = 0, message = "就业状态不合法")
    @Max(value = 5, message = "就业状态不合法")
    private Integer employmentStatus;

    @NotBlank(message = "就业类型不能为空")
    @Size(max = 64, message = "就业类型长度不能超过64位")
    private String employmentType;

    @NotBlank(message = "就业单位名称不能为空")
    @Size(max = 255, message = "就业单位名称长度不能超过255位")
    private String companyName;

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 128, message = "岗位名称长度不能超过128位")
    private String jobTitle;

    @Size(max = 128, message = "就业城市长度不能超过128位")
    private String workCity;

    private LocalDate contractStartDate;

    @DecimalMin(value = "0.00", message = "薪资金额不能小于0")
    private BigDecimal salaryAmount;

    @Size(max = 64, message = "填报来源长度不能超过64位")
    private String reportSource;

    private Long jobPostId;

    private Long enterpriseId;
}
