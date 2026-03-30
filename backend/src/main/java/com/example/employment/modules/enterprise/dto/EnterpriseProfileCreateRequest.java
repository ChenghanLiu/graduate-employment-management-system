package com.example.employment.modules.enterprise.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EnterpriseProfileCreateRequest {

    @NotBlank(message = "企业名称不能为空")
    @Size(max = 255, message = "企业名称长度不能超过255位")
    private String enterpriseName;

    @NotBlank(message = "统一社会信用代码不能为空")
    @Pattern(regexp = "^[0-9A-Z]{18}$", message = "统一社会信用代码格式不正确")
    private String creditCode;

    @Size(max = 128, message = "所属行业长度不能超过128位")
    private String industryName;

    @Size(max = 64, message = "企业规模长度不能超过64位")
    private String enterpriseScale;

    @NotBlank(message = "联系人不能为空")
    @Size(max = 64, message = "联系人长度不能超过64位")
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    @Size(max = 32, message = "联系电话长度不能超过32位")
    private String contactPhone;

    @Email(message = "联系邮箱格式不正确")
    @Size(max = 128, message = "联系邮箱长度不能超过128位")
    private String contactEmail;

    @Size(max = 255, message = "企业地址长度不能超过255位")
    private String address;

    @Size(max = 1000, message = "企业简介长度不能超过1000位")
    private String introduction;
}
