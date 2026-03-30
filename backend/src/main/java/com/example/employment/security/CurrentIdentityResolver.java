package com.example.employment.security;

import com.example.employment.common.enums.RoleCode;
import com.example.employment.entity.SysRole;
import com.example.employment.entity.SysUserRole;
import com.example.employment.mapper.SysRoleMapper;
import com.example.employment.mapper.SysUserRoleMapper;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

@Component
public class CurrentIdentityResolver {

    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USER_ROLE_HEADER = "X-User-Role";

    private final Long defaultStudentUserId;
    private final Long defaultEnterpriseUserId;
    private final Long defaultCounselorUserId;
    private final Long defaultEmploymentTeacherUserId;
    private final Long defaultAdminUserId;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    public CurrentIdentityResolver(
            @Value("${app.current-user.student-id:1001}") Long defaultStudentUserId,
            @Value("${app.current-user.enterprise-id:2001}") Long defaultEnterpriseUserId,
            @Value("${app.current-user.counselor-id:3002}") Long defaultCounselorUserId,
            @Value("${app.current-user.employment-teacher-id:3001}") Long defaultEmploymentTeacherUserId,
            @Value("${app.current-user.admin-id:9001}") Long defaultAdminUserId,
            SysUserRoleMapper sysUserRoleMapper,
            SysRoleMapper sysRoleMapper) {
        this.defaultStudentUserId = defaultStudentUserId;
        this.defaultEnterpriseUserId = defaultEnterpriseUserId;
        this.defaultCounselorUserId = defaultCounselorUserId;
        this.defaultEmploymentTeacherUserId = defaultEmploymentTeacherUserId;
        this.defaultAdminUserId = defaultAdminUserId;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    public CurrentIdentity resolve(HttpServletRequest request) {
        RoleCode role = resolveRole(request);
        Long userId = resolveUserId(request, role);
        return new CurrentIdentity(userId, role);
    }

    private RoleCode resolveRole(HttpServletRequest request) {
        String headerValue = request.getHeader(USER_ROLE_HEADER);
        if (StringUtils.hasText(headerValue)) {
            return RoleCode.from(headerValue.trim());
        }

        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (StringUtils.hasText(userIdHeader)) {
            Long userId = parseUserId(userIdHeader);
            return inferRoleByUserId(userId);
        }

        return RoleCode.STUDENT;
    }

    private Long resolveUserId(HttpServletRequest request, RoleCode role) {
        String headerValue = request.getHeader(USER_ID_HEADER);
        if (!StringUtils.hasText(headerValue)) {
            return defaultUserId(role);
        }
        return parseUserId(headerValue);
    }

    private Long parseUserId(String headerValue) {
        try {
            long userId = Long.parseLong(headerValue.trim());
            if (userId <= 0) {
                throw new IllegalArgumentException("X-User-Id 必须为正整数");
            }
            return userId;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("X-User-Id 必须为正整数");
        }
    }

    private RoleCode inferRoleByUserId(Long userId) {
        if (defaultStudentUserId.equals(userId)) {
            return RoleCode.STUDENT;
        }
        if (defaultEnterpriseUserId.equals(userId)) {
            return RoleCode.ENTERPRISE;
        }
        if (defaultEmploymentTeacherUserId.equals(userId)) {
            return RoleCode.EMPLOYMENT_TEACHER;
        }
        if (defaultCounselorUserId.equals(userId)) {
            return RoleCode.COUNSELOR;
        }
        if (defaultAdminUserId.equals(userId)) {
            return RoleCode.ADMIN;
        }
        RoleCode mappedRole = findPrimaryRoleByUserId(userId);
        if (mappedRole != null) {
            return mappedRole;
        }
        throw new IllegalArgumentException("缺少 X-User-Role，且无法根据 X-User-Id 推断角色");
    }

    private Long defaultUserId(RoleCode role) {
        return switch (role) {
            case STUDENT -> defaultStudentUserId;
            case ENTERPRISE -> defaultEnterpriseUserId;
            case COUNSELOR -> defaultCounselorUserId;
            case EMPLOYMENT_TEACHER -> defaultEmploymentTeacherUserId;
            case ADMIN -> defaultAdminUserId;
        };
    }

    private RoleCode findPrimaryRoleByUserId(Long userId) {
        List<SysUserRole> bindings = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getStatus, 1));
        if (bindings.isEmpty()) {
            return null;
        }

        Map<Long, SysRole> roleMap = sysRoleMapper.selectBatchIds(bindings.stream()
                        .map(SysUserRole::getRoleId)
                        .distinct()
                        .toList())
                .stream()
                .collect(Collectors.toMap(SysRole::getId, Function.identity()));

        return bindings.stream()
                .map(binding -> roleMap.get(binding.getRoleId()))
                .filter(role -> role != null && role.getStatus() != null && role.getStatus() == 1)
                .map(role -> RoleCode.from(role.getRoleCode()))
                .min(Comparator.comparingInt(this::rolePriority))
                .orElse(null);
    }

    private int rolePriority(RoleCode roleCode) {
        return switch (roleCode) {
            case ADMIN -> 0;
            case EMPLOYMENT_TEACHER -> 1;
            case COUNSELOR -> 2;
            case ENTERPRISE -> 3;
            case STUDENT -> 4;
        };
    }
}
