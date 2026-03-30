package com.example.employment.modules.auth.vo;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthUserVO {

    private final Long userId;

    private final String username;

    private final String realName;

    private final String roleCode;

    private final String roleName;

    private final List<String> roleCodes;
}
