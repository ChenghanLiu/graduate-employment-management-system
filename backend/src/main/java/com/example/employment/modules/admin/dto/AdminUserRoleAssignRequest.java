package com.example.employment.modules.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Data;

@Data
public class AdminUserRoleAssignRequest {

    @NotNull(message = "角色ID列表不能为空")
    private List<@Valid @Positive(message = "角色ID必须为正整数") Long> roleIds;
}
