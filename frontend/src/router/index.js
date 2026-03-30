import { createRouter, createWebHistory } from 'vue-router'
import RoleLayout from '../layouts/RoleLayout.vue'
import NotFoundView from '../views/common/NotFoundView.vue'
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'
import StudentDashboardView from '../views/student/StudentDashboardView.vue'
import StudentProfileView from '../views/student/StudentProfileView.vue'
import StudentResumeView from '../views/student/StudentResumeView.vue'
import StudentJobsView from '../views/student/StudentJobsView.vue'
import StudentJobDetailView from '../views/student/StudentJobDetailView.vue'
import StudentApplicationsView from '../views/student/StudentApplicationsView.vue'
import StudentEmploymentView from '../views/student/StudentEmploymentView.vue'
import StudentAnnouncementsView from '../views/student/StudentAnnouncementsView.vue'
import StudentAnnouncementDetailView from '../views/student/StudentAnnouncementDetailView.vue'
import EnterpriseDashboardView from '../views/enterprise/EnterpriseDashboardView.vue'
import EnterpriseProfileView from '../views/enterprise/EnterpriseProfileView.vue'
import EnterpriseJobsView from '../views/enterprise/EnterpriseJobsView.vue'
import EnterpriseApplicationsView from '../views/enterprise/EnterpriseApplicationsView.vue'
import AdminDashboardView from '../views/admin/AdminDashboardView.vue'
import AdminStudentsView from '../views/admin/AdminStudentsView.vue'
import AdminEnterprisesView from '../views/admin/AdminEnterprisesView.vue'
import AdminUsersView from '../views/admin/AdminUsersView.vue'
import AdminAnnouncementsView from '../views/admin/AdminAnnouncementsView.vue'
import AdminEmploymentReviewView from '../views/admin/AdminEmploymentReviewView.vue'
import { ROLE_CODES } from '../constants/identity'
import { getCurrentIdentity, isAuthenticated } from '../utils/identity'

const studentNavItems = [
  { path: '/student/dashboard', label: '学生工作台', roles: [ROLE_CODES.STUDENT] },
  { path: '/student/profile', label: '学生档案', roles: [ROLE_CODES.STUDENT] },
  { path: '/student/resume', label: '简历管理', roles: [ROLE_CODES.STUDENT] },
  { path: '/student/jobs', label: '岗位列表', roles: [ROLE_CODES.STUDENT] },
  { path: '/student/applications', label: '我的投递', roles: [ROLE_CODES.STUDENT] },
  { path: '/student/employment', label: '就业登记', roles: [ROLE_CODES.STUDENT] },
  { path: '/student/announcements', label: '公告中心', roles: [ROLE_CODES.STUDENT] }
]

const enterpriseNavItems = [
  { path: '/enterprise/dashboard', label: '企业工作台', roles: [ROLE_CODES.ENTERPRISE] },
  { path: '/enterprise/profile', label: '企业档案', roles: [ROLE_CODES.ENTERPRISE] },
  { path: '/enterprise/jobs', label: '岗位管理', roles: [ROLE_CODES.ENTERPRISE] },
  { path: '/enterprise/applications', label: '投递管理', roles: [ROLE_CODES.ENTERPRISE] }
]

const adminNavItems = [
  { path: '/admin/dashboard', label: '管理概览', roles: [ROLE_CODES.ADMIN, ROLE_CODES.EMPLOYMENT_TEACHER] },
  { path: '/admin/students', label: '学生管理', roles: [ROLE_CODES.ADMIN] },
  { path: '/admin/enterprises', label: '企业管理', roles: [ROLE_CODES.ADMIN] },
  { path: '/admin/users', label: '用户管理', roles: [ROLE_CODES.ADMIN] },
  { path: '/admin/announcements', label: '公告管理', roles: [ROLE_CODES.ADMIN] },
  { path: '/admin/employment-review', label: '就业审核', roles: [ROLE_CODES.ADMIN, ROLE_CODES.EMPLOYMENT_TEACHER] }
]

const routes = [
  {
    path: '/',
    redirect: () => (isAuthenticated() ? getCurrentIdentity().defaultPath : '/login')
  },
  {
    path: '/login',
    component: LoginView,
    meta: {
      guestOnly: true
    }
  },
  {
    path: '/register',
    component: RegisterView,
    meta: {
      guestOnly: true
    }
  },
  {
    path: '/student',
    component: RoleLayout,
    props: {
      navItems: studentNavItems,
      layoutTitle: '学生端'
    },
    meta: {
      requiresAuth: true,
      roles: [ROLE_CODES.STUDENT]
    },
    children: [
      { path: '', redirect: '/student/dashboard' },
      { path: 'dashboard', component: StudentDashboardView },
      { path: 'profile', component: StudentProfileView },
      { path: 'resume', component: StudentResumeView },
      { path: 'jobs', component: StudentJobsView },
      { path: 'jobs/:id', component: StudentJobDetailView },
      { path: 'applications', component: StudentApplicationsView },
      { path: 'employment', component: StudentEmploymentView },
      { path: 'announcements', component: StudentAnnouncementsView },
      { path: 'announcements/:id', component: StudentAnnouncementDetailView }
    ]
  },
  {
    path: '/enterprise',
    component: RoleLayout,
    props: {
      navItems: enterpriseNavItems,
      layoutTitle: '企业端'
    },
    meta: {
      requiresAuth: true,
      roles: [ROLE_CODES.ENTERPRISE]
    },
    children: [
      { path: '', redirect: '/enterprise/dashboard' },
      { path: 'dashboard', component: EnterpriseDashboardView },
      { path: 'profile', component: EnterpriseProfileView },
      { path: 'jobs', component: EnterpriseJobsView },
      { path: 'applications', component: EnterpriseApplicationsView }
    ]
  },
  {
    path: '/admin',
    component: RoleLayout,
    props: {
      navItems: adminNavItems,
      layoutTitle: '管理端'
    },
    meta: {
      requiresAuth: true,
      roles: [ROLE_CODES.ADMIN, ROLE_CODES.EMPLOYMENT_TEACHER]
    },
    children: [
      { path: '', redirect: '/admin/dashboard' },
      { path: 'dashboard', component: AdminDashboardView },
      { path: 'students', component: AdminStudentsView, meta: { roles: [ROLE_CODES.ADMIN] } },
      { path: 'enterprises', component: AdminEnterprisesView, meta: { roles: [ROLE_CODES.ADMIN] } },
      { path: 'users', component: AdminUsersView, meta: { roles: [ROLE_CODES.ADMIN] } },
      { path: 'announcements', component: AdminAnnouncementsView, meta: { roles: [ROLE_CODES.ADMIN] } },
      { path: 'employment-review', component: AdminEmploymentReviewView }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    component: NotFoundView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const identity = getCurrentIdentity()
  const authenticated = isAuthenticated()
  const requiresAuth = to.matched.some((item) => item.meta.requiresAuth)
  const requiredRoles = to.matched.flatMap((item) => item.meta.roles || [])
  const guestOnly = to.matched.some((item) => item.meta.guestOnly)

  if (guestOnly && authenticated) {
    next(identity.defaultPath)
    return
  }

  if (requiresAuth && !authenticated) {
    next({
      path: '/login',
      query: to.fullPath && to.fullPath !== '/' ? { redirect: to.fullPath } : undefined
    })
    return
  }

  if (!requiredRoles.length || (authenticated && requiredRoles.includes(identity.roleCode))) {
    next()
    return
  }

  next(authenticated ? identity.defaultPath : '/login')
})

export default router
