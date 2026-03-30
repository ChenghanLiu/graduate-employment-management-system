package com.example.employment.modules.jobpost.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class JobPostCreateRequest {

    @NotBlank(message = "岗位名称不能为空")
    @Size(max = 128, message = "岗位名称长度不能超过128位")
    private String jobTitle;

    @Size(max = 128, message = "岗位类别长度不能超过128位")
    private String jobCategory;

    @NotBlank(message = "工作城市不能为空")
    @Size(max = 128, message = "工作城市长度不能超过128位")
    private String workCity;

    @Size(max = 64, message = "学历要求长度不能超过64位")
    private String educationRequirement;

    @DecimalMin(value = "0.00", message = "最低薪资不能小于0")
    private BigDecimal salaryMin;

    @DecimalMin(value = "0.00", message = "最高薪资不能小于0")
    private BigDecimal salaryMax;

    @Size(max = 64, message = "薪资说明长度不能超过64位")
    private String salaryRemark;

    @Min(value = 1, message = "招聘人数至少为1")
    private Integer hiringCount;

    @Size(max = 2000, message = "岗位描述长度不能超过2000位")
    private String jobDescription;

    private LocalDate deadline;
}
