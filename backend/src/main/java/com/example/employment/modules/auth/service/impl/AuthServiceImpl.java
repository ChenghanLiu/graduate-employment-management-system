package com.example.employment.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.employment.common.BusinessException;
import com.example.employment.common.enums.EnterpriseReviewStatusEnum;
import com.example.employment.common.enums.RoleCode;
import com.example.employment.entity.EnterpriseProfile;
import com.example.employment.entity.StudentProfile;
import com.example.employment.entity.SysRole;
import com.example.employment.entity.SysUser;
import com.example.employment.entity.SysUserRole;
import com.example.employment.mapper.EnterpriseProfileMapper;
import com.example.employment.mapper.StudentProfileMapper;
import com.example.employment.mapper.SysRoleMapper;
import com.example.employment.mapper.SysUserMapper;
import com.example.employment.mapper.SysUserRoleMapper;
import com.example.employment.modules.auth.dto.AuthLoginRequest;
import com.example.employment.modules.auth.dto.EnterpriseRegisterRequest;
import com.example.employment.modules.auth.dto.StudentRegisterRequest;
import com.example.employment.modules.auth.service.AuthService;
import com.example.employment.modules.auth.vo.AuthUserVO;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final EnterpriseProfileMapper enterpriseProfileMapper;

    public AuthServiceImpl(SysUserMapper sysUserMapper,
                           SysRoleMapper sysRoleMapper,
                           SysUserRoleMapper sysUserRoleMapper,
                           StudentProfileMapper studentProfileMapper,
                           EnterpriseProfileMapper enterpriseProfileMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
        this.studentProfileMapper = studentProfileMapper;
        this.enterpriseProfileMapper = enterpriseProfileMapper;
    }

    @Override
    public AuthUserVO login(AuthLoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, normalize(request.getUsername()))
                .last("LIMIT 1"));
        if (user == null || !normalize(request.getPassword()).equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("当前账号已被禁用");
        }
        return buildAuthUser(user);
    }

    @Override
    @Transactional
    public void registerStudent(StudentRegisterRequest request) {
        validateUsernameAvailable(request.getUsername());
        validateStudentNoAvailable(request.getStudentNo());

        SysUser user = createBaseUser(
                request.getUsername(),
                request.getPassword(),
                request.getRealName(),
                request.getPhone(),
                request.getEmail()
        );
        bindRole(user.getId(), RoleCode.STUDENT);

        StudentProfile profile = new StudentProfile();
        profile.setUserId(user.getId());
        profile.setStudentNo(normalize(request.getStudentNo()));
        profile.setCollegeName(normalize(request.getCollegeName()));
        profile.setMajorName(normalize(request.getMajorName()));
        profile.setClassName(normalizeNullable(request.getClassName()));
        profile.setEducationLevel(normalize(request.getEducationLevel()));
        profile.setGraduationYear(request.getGraduationYear());
        profile.setEmploymentStatus(0);
        profile.setStatus(1);
        studentProfileMapper.insert(profile);
    }

    @Override
    @Transactional
    public void registerEnterprise(EnterpriseRegisterRequest request) {
        validateUsernameAvailable(request.getUsername());
        validateCreditCodeAvailable(request.getCreditCode());

        SysUser user = createBaseUser(
                request.getUsername(),
                request.getPassword(),
                request.getEnterpriseName(),
                request.getContactPhone(),
                request.getContactEmail()
        );
        bindRole(user.getId(), RoleCode.ENTERPRISE);

        EnterpriseProfile profile = new EnterpriseProfile();
        profile.setUserId(user.getId());
        profile.setEnterpriseName(normalize(request.getEnterpriseName()));
        profile.setCreditCode(normalize(request.getCreditCode()));
        profile.setContactName(normalize(request.getContactName()));
        profile.setContactPhone(normalize(request.getContactPhone()));
        profile.setContactEmail(normalize(request.getContactEmail()));
        profile.setReviewStatus(EnterpriseReviewStatusEnum.PENDING.getCode());
        profile.setReviewRemark("注册成功，待管理员审核");
        profile.setStatus(1);
        enterpriseProfileMapper.insert(profile);
    }

    @Override
    public AuthUserVO getCurrentUser(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || user.getStatus() == null || user.getStatus() != 1) {
            throw new BusinessException("当前登录状态无效");
        }
        return buildAuthUser(user);
    }

    private AuthUserVO buildAuthUser(SysUser user) {
        List<SysUserRole> bindings = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId())
                .eq(SysUserRole::getStatus, 1));
        if (bindings.isEmpty()) {
            throw new BusinessException("当前账号未配置角色");
        }

        Map<Long, SysRole> roleMap = sysRoleMapper.selectBatchIds(bindings.stream()
                        .map(SysUserRole::getRoleId)
                        .distinct()
                        .toList())
                .stream()
                .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                .collect(Collectors.toMap(SysRole::getId, Function.identity()));

        List<RoleCode> roleCodes = bindings.stream()
                .map(binding -> roleMap.get(binding.getRoleId()))
                .filter(role -> role != null)
                .map(role -> RoleCode.from(role.getRoleCode()))
                .sorted(Comparator.comparingInt(this::rolePriority))
                .toList();

        if (roleCodes.isEmpty()) {
            throw new BusinessException("当前账号未配置可用角色");
        }

        RoleCode primaryRole = roleCodes.get(0);
        SysRole primarySysRole = roleMap.values().stream()
                .filter(role -> RoleCode.from(role.getRoleCode()) == primaryRole)
                .findFirst()
                .orElseThrow(() -> new BusinessException("当前账号角色配置异常"));

        return AuthUserVO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleCode(primaryRole.name())
                .roleName(primarySysRole.getRoleName())
                .roleCodes(roleCodes.stream().map(Enum::name).toList())
                .build();
    }

    private SysUser createBaseUser(String username, String password, String realName, String phone, String email) {
        SysUser user = new SysUser();
        user.setUsername(normalize(username));
        user.setPassword(normalize(password));
        user.setRealName(normalize(realName));
        user.setPhone(normalize(phone));
        user.setEmail(normalize(email));
        user.setStatus(1);
        sysUserMapper.insert(user);
        return user;
    }

    private void bindRole(Long userId, RoleCode roleCode) {
        SysRole role = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRoleCode, roleCode.name())
                .eq(SysRole::getStatus, 1)
                .last("LIMIT 1"));
        if (role == null) {
            throw new IllegalStateException("系统角色未初始化: " + roleCode.name());
        }

        SysUserRole binding = new SysUserRole();
        binding.setUserId(userId);
        binding.setRoleId(role.getId());
        binding.setStatus(1);
        sysUserRoleMapper.insert(binding);
    }

    private void validateUsernameAvailable(String username) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, normalize(username)));
        if (count != null && count > 0) {
            throw new BusinessException("用户名已存在");
        }
    }

    private void validateStudentNoAvailable(String studentNo) {
        Long count = studentProfileMapper.selectCount(new LambdaQueryWrapper<StudentProfile>()
                .eq(StudentProfile::getStudentNo, normalize(studentNo)));
        if (count != null && count > 0) {
            throw new BusinessException("学号已存在");
        }
    }

    private void validateCreditCodeAvailable(String creditCode) {
        Long count = enterpriseProfileMapper.selectCount(new LambdaQueryWrapper<EnterpriseProfile>()
                .eq(EnterpriseProfile::getCreditCode, normalize(creditCode)));
        if (count != null && count > 0) {
            throw new BusinessException("统一社会信用代码已存在");
        }
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        String normalized = normalize(value);
        return StringUtils.hasText(normalized) ? normalized : null;
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
