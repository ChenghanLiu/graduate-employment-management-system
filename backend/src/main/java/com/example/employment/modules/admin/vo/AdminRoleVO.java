package com.example.employment.modules.admin.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminRoleVO {

    private final Long id;

    private final String roleCode;

    private final String roleName;

    private final String remark;

    private final Integer status;
}
