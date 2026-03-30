package com.example.employment.modules.jobapplication.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobApplicationCreateRequest {

    @NotNull(message = "岗位ID不能为空")
    private Long jobPostId;

    @Size(max = 255, message = "备注长度不能超过255位")
    private String remark;
}
