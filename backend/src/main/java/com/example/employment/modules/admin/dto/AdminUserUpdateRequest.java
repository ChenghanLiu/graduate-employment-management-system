package com.example.employment.modules.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminUserUpdateRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64位")
    private String username;

    @Size(max = 255, message = "密码长度不能超过255位")
    private String password;

    @Size(max = 64, message = "真实姓名长度不能超过64位")
    private String realName;

    @Size(max = 32, message = "手机号长度不能超过32位")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128位")
    private String email;

    private Integer status;
}
