package com.example.employment.modules.announcement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnnouncementUpdateRequest {

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 255, message = "公告标题长度不能超过255位")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(max = 10000, message = "公告内容长度不能超过10000位")
    private String content;

    @NotBlank(message = "公告类型不能为空")
    @Size(max = 64, message = "公告类型长度不能超过64位")
    private String type;
}
