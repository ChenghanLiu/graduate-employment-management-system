package com.example.employment.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StudentRegisterRequest {

    @NotBlank(message = "请输入用户名")
    private String username;

    @NotBlank(message = "请输入密码")
    private String password;

    @NotBlank(message = "请输入真实姓名")
    private String realName;

    @NotBlank(message = "请输入手机号")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String phone;

    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "请输入邮箱")
    private String email;

    @NotBlank(message = "请输入学号")
    private String studentNo;

    @NotBlank(message = "请输入学院")
    private String collegeName;

    @NotBlank(message = "请输入专业")
    private String majorName;

    private String className;

    @NotBlank(message = "请输入学历层次")
    private String educationLevel;

    @NotNull(message = "请输入毕业年份")
    @Min(value = 2000, message = "毕业年份不正确")
    @Max(value = 2100, message = "毕业年份不正确")
    private Integer graduationYear;
}
