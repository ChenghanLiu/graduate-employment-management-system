package com.example.employment.modules.admin.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserVO {

    private final Long id;

    private final String username;

    private final String realName;

    private final String phone;

    private final String email;

    private final Integer status;

    private final String passwordStrategy;

    private final LocalDateTime createTime;

    private final LocalDateTime updateTime;

    private final List<AdminRoleVO> roles;
}
