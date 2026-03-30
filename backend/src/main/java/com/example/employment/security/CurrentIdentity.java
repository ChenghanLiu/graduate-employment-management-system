package com.example.employment.security;

import com.example.employment.common.enums.RoleCode;

public record CurrentIdentity(Long userId, RoleCode role) {
}
