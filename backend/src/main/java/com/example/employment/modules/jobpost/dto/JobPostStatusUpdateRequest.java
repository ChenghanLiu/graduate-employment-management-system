package com.example.employment.modules.jobpost.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobPostStatusUpdateRequest {

    @NotNull(message = "岗位状态不能为空")
    private Integer postStatus;
}
