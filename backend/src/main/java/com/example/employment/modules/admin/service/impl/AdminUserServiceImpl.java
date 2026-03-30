package com.example.employment.modules.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.ApiCode;
import com.example.employment.common.BusinessException;
import com.example.employment.entity.SysRole;
import com.example.employment.entity.SysUser;
import com.example.employment.entity.SysUserRole;
import com.example.employment.mapper.SysRoleMapper;
import com.example.employment.mapper.SysUserMapper;
import com.example.employment.mapper.SysUserRoleMapper;
import com.example.employment.modules.admin.dto.AdminUserCreateRequest;
import com.example.employment.modules.admin.dto.AdminUserRoleAssignRequest;
import com.example.employment.modules.admin.dto.AdminUserUpdateRequest;
import com.example.employment.modules.admin.service.AdminUserService;
import com.example.employment.modules.admin.vo.AdminRoleVO;
import com.example.employment.modules.admin.vo.AdminUserVO;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final int STATUS_ENABLED = 1;
    private static final int STATUS_DISABLED = 0;
    private static final String DEFAULT_PASSWORD = "ChangeMe123!";
    private static final String DEFAULT_PASSWORD_STRATEGY = "未传 password 时使用默认密码 ChangeMe123!";

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public AdminUserServiceImpl(SysUserMapper sysUserMapper,
                                SysRoleMapper sysRoleMapper,
                                SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public List<AdminUserVO> listUsers() {
        List<SysUser> users = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .orderByDesc(SysUser::getCreateTime)
                .orderByDesc(SysUser::getId));
        Map<Long, List<AdminRoleVO>> userRoles = buildUserRoleMap(users.stream().map(SysUser::getId).toList());
        return users.stream()
                .map(user -> toUserVO(user, userRoles.getOrDefault(user.getId(), List.of())))
                .toList();
    }

    @Override
    public AdminUserVO getUser(Long userId) {
        SysUser user = getUserOrThrow(userId);
        Map<Long, List<AdminRoleVO>> userRoles = buildUserRoleMap(List.of(userId));
        return toUserVO(user, userRoles.getOrDefault(userId, List.of()));
    }

    @Override
    public AdminUserVO createUser(AdminUserCreateRequest request) {
        ensureUsernameUnique(request.getUsername(), null);
        SysUser user = new SysUser();
        fillUser(user, request.getUsername(), request.getPassword(), request.getRealName(),
                request.getPhone(), request.getEmail(), request.getStatus());
        sysUserMapper.insert(user);
        return toUserVO(user, List.of());
    }

    @Override
    public AdminUserVO updateUser(Long userId, AdminUserUpdateRequest request) {
        SysUser user = getUserOrThrow(userId);
        ensureUsernameUnique(request.getUsername(), userId);
        fillUser(user, request.getUsername(), request.getPassword(), request.getRealName(),
                request.getPhone(), request.getEmail(), request.getStatus());
        sysUserMapper.updateById(user);
        return getUser(userId);
    }

    @Override
    public void disableUser(Long userId) {
        SysUser user = getUserOrThrow(userId);
        user.setStatus(STATUS_DISABLED);
        sysUserMapper.updateById(user);

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getStatus, STATUS_ENABLED));
        for (SysUserRole userRole : userRoles) {
            userRole.setStatus(STATUS_DISABLED);
            sysUserRoleMapper.updateById(userRole);
        }
    }

    @Override
    public AdminUserVO assignRoles(Long userId, AdminUserRoleAssignRequest request) {
        SysUser user = getActiveUserOrThrow(userId);
        List<Long> requestedRoleIds = deduplicateRoleIds(request.getRoleIds());
        ensureRolesExist(requestedRoleIds);

        List<SysUserRole> currentRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId));

        for (SysUserRole currentRole : currentRoles) {
            if (currentRole.getStatus() != null && currentRole.getStatus() == STATUS_ENABLED) {
                currentRole.setStatus(STATUS_DISABLED);
                sysUserRoleMapper.updateById(currentRole);
            }
        }

        for (Long roleId : requestedRoleIds) {
            SysUserRole existing = currentRoles.stream()
                    .filter(item -> roleId.equals(item.getRoleId()))
                    .findFirst()
                    .orElse(null);
            if (existing != null) {
                existing.setStatus(STATUS_ENABLED);
                sysUserRoleMapper.updateById(existing);
                continue;
            }

            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(roleId);
            userRole.setStatus(STATUS_ENABLED);
            sysUserRoleMapper.insert(userRole);
        }

        return getUser(userId);
    }

    @Override
    public List<AdminRoleVO> listRoles() {
        return sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, STATUS_ENABLED)
                        .orderByAsc(SysRole::getId))
                .stream()
                .map(this::toRoleVO)
                .toList();
    }

    private void ensureUsernameUnique(String username, Long excludeId) {
        SysUser existing = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username.trim())
                .ne(excludeId != null, SysUser::getId, excludeId)
                .last("limit 1"));
        if (existing != null) {
            throw new BusinessException(ApiCode.CONFLICT, "用户名已存在");
        }
    }

    private SysUser getUserOrThrow(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    private SysUser getActiveUserOrThrow(Long userId) {
        SysUser user = getUserOrThrow(userId);
        if (user.getStatus() == null || user.getStatus() != STATUS_ENABLED) {
            throw new BusinessException(ApiCode.ILLEGAL_STATE, "禁用用户不允许分配角色");
        }
        return user;
    }

    private List<Long> deduplicateRoleIds(List<Long> roleIds) {
        if (roleIds == null) {
            return List.of();
        }
        return new ArrayList<>(new LinkedHashSet<>(roleIds));
    }

    private void ensureRolesExist(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        List<SysRole> roles = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .in(SysRole::getId, roleIds)
                .eq(SysRole::getStatus, STATUS_ENABLED));
        if (roles.size() != roleIds.size()) {
            throw new BusinessException(ApiCode.BAD_REQUEST, "角色不存在或已禁用");
        }
    }

    private Map<Long, List<AdminRoleVO>> buildUserRoleMap(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, userIds)
                .eq(SysUserRole::getStatus, STATUS_ENABLED));
        if (userRoles.isEmpty()) {
            return Map.of();
        }

        Set<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(LinkedHashSet::new, Set::add, Set::addAll);
        Map<Long, AdminRoleVO> roleMap = sysRoleMapper.selectList(new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getId, roleIds))
                .stream()
                .map(this::toRoleVO)
                .collect(LinkedHashMap::new, (map, role) -> map.put(role.getId(), role), Map::putAll);

        Map<Long, List<AdminRoleVO>> result = new LinkedHashMap<>();
        for (SysUserRole userRole : userRoles) {
            AdminRoleVO role = roleMap.get(userRole.getRoleId());
            if (role == null) {
                continue;
            }
            result.computeIfAbsent(userRole.getUserId(), key -> new ArrayList<>()).add(role);
        }
        return result;
    }

    private void fillUser(SysUser user,
                          String username,
                          String password,
                          String realName,
                          String phone,
                          String email,
                          Integer status) {
        user.setUsername(username.trim());
        if (StringUtils.hasText(password)) {
            user.setPassword(password.trim());
        } else if (user.getId() == null) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        user.setRealName(trimToNull(realName));
        user.setPhone(trimToNull(phone));
        user.setEmail(trimToNull(email));
        user.setStatus(status == null ? STATUS_ENABLED : normalizeStatus(status));
    }

    private int normalizeStatus(Integer status) {
        if (status == STATUS_ENABLED || status == STATUS_DISABLED) {
            return status;
        }
        throw new BusinessException(ApiCode.BAD_REQUEST, "用户状态仅支持1或0");
    }

    private AdminRoleVO toRoleVO(SysRole role) {
        return AdminRoleVO.builder()
                .id(role.getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .remark(role.getRemark())
                .status(role.getStatus())
                .build();
    }

    private AdminUserVO toUserVO(SysUser user, List<AdminRoleVO> roles) {
        return AdminUserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .status(user.getStatus())
                .passwordStrategy(DEFAULT_PASSWORD_STRATEGY)
                .createTime(user.getCreateTime())
                .updateTime(user.getUpdateTime())
                .roles(roles)
                .build();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
