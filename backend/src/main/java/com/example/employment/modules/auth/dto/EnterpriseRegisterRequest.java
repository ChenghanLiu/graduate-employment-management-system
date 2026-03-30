package com.example.employment.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EnterpriseRegisterRequest {

    @NotBlank(message = "请输入用户名")
    private String username;

    @NotBlank(message = "请输入密码")
    private String password;

    @NotBlank(message = "请输入企业名称")
    private String enterpriseName;

    @NotBlank(message = "请输入联系人")
    private String contactName;

    @NotBlank(message = "请输入联系电话")
    @Pattern(regexp = "^1\\d{10}$", message = "联系电话格式不正确")
    private String contactPhone;

    @Email(message = "联系邮箱格式不正确")
    @NotBlank(message = "请输入联系邮箱")
    private String contactEmail;

    @NotBlank(message = "请输入统一社会信用代码")
    private String creditCode;
}
