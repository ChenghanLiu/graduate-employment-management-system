package com.example.employment.modules.jobapplication.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JobApplicationStatusUpdateRequest {

    @NotNull(message = "投递状态不能为空")
    private Integer applicationStatus;

    @Size(max = 255, message = "备注长度不能超过255位")
    private String remark;
}
