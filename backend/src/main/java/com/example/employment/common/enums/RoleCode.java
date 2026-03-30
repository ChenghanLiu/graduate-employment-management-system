package com.example.employment.common.enums;

import java.util.Arrays;

public enum RoleCode {
    STUDENT,
    ENTERPRISE,
    COUNSELOR,
    EMPLOYMENT_TEACHER,
    ADMIN;

    public static RoleCode from(String value) {
        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("X-User-Role 不合法"));
    }
}
