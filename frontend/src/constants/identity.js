export const ROLE_CODES = {
  STUDENT: 'STUDENT',
  ENTERPRISE: 'ENTERPRISE',
  COUNSELOR: 'COUNSELOR',
  EMPLOYMENT_TEACHER: 'EMPLOYMENT_TEACHER',
  ADMIN: 'ADMIN'
}

export const ROLE_LABELS = {
  [ROLE_CODES.STUDENT]: '学生',
  [ROLE_CODES.ENTERPRISE]: '企业',
  [ROLE_CODES.COUNSELOR]: '辅导员',
  [ROLE_CODES.EMPLOYMENT_TEACHER]: '就业教师',
  [ROLE_CODES.ADMIN]: '管理员'
}

export const ROLE_HOME_PATHS = {
  [ROLE_CODES.STUDENT]: '/student/dashboard',
  [ROLE_CODES.ENTERPRISE]: '/enterprise/dashboard',
  [ROLE_CODES.COUNSELOR]: '/admin/dashboard',
  [ROLE_CODES.EMPLOYMENT_TEACHER]: '/admin/dashboard',
  [ROLE_CODES.ADMIN]: '/admin/dashboard'
}

export function resolveRoleHomePath(roleCode) {
  return ROLE_HOME_PATHS[roleCode] || '/login'
}
