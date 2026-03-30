package com.example.employment.modules.student.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentProfileCreateRequest {

    @NotBlank(message = "学号不能为空")
    @Size(max = 32, message = "学号长度不能超过32位")
    private String studentNo;

    @Min(value = 1, message = "性别值不合法")
    @Max(value = 2, message = "性别值不合法")
    private Integer gender;

    @NotBlank(message = "学院名称不能为空")
    @Size(max = 128, message = "学院名称长度不能超过128位")
    private String collegeName;

    @NotBlank(message = "专业名称不能为空")
    @Size(max = 128, message = "专业名称长度不能超过128位")
    private String majorName;

    @Size(max = 128, message = "班级名称长度不能超过128位")
    private String className;

    @NotBlank(message = "学历层次不能为空")
    @Size(max = 64, message = "学历层次长度不能超过64位")
    private String educationLevel;

    @NotNull(message = "毕业年份不能为空")
    @Min(value = 2000, message = "毕业年份不合法")
    @Max(value = 2100, message = "毕业年份不合法")
    private Integer graduationYear;

    @Size(max = 128, message = "生源地长度不能超过128位")
    private String nativePlace;

    @NotNull(message = "就业状态不能为空")
    @Min(value = 0, message = "就业状态不合法")
    @Max(value = 5, message = "就业状态不合法")
    private Integer employmentStatus;
}
