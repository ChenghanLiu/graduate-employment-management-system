package com.example.employment.modules.announcement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementStatusUpdateRequest {

    @NotNull(message = "公告状态不能为空")
    private Integer status;
}
