package com.example.employment.modules.employmentattachment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmploymentAttachmentCreateRequest {

    @NotBlank(message = "文件名称不能为空")
    @Size(max = 255, message = "文件名称长度不能超过255位")
    private String fileName;

    @NotBlank(message = "文件地址不能为空")
    @Size(max = 500, message = "文件地址长度不能超过500位")
    private String fileUrl;

    @NotBlank(message = "文件类型不能为空")
    @Size(max = 64, message = "文件类型长度不能超过64位")
    private String fileType;

    @Size(max = 255, message = "备注长度不能超过255位")
    private String remark;
}
