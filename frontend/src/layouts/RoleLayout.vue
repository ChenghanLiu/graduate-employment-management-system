<template>
  <div class="role-layout">
    <aside class="role-layout__aside">
      <div class="brand-block">
        <p class="brand-block__eyebrow">Graduate Employment</p>
        <h1>毕业生就业管理系统</h1>
        <p class="brand-block__role">{{ layoutTitle }}</p>
      </div>
      <el-menu
        :default-active="route.path"
        class="nav-menu"
        router
      >
        <el-menu-item v-for="item in visibleNavItems" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
    </aside>
    <div class="role-layout__main">
      <header class="topbar page-card">
        <div>
          <div class="topbar__title">{{ currentNavLabel }}</div>
          <div class="topbar__meta">
            <el-tag type="success" effect="light">{{ currentIdentity.roleName }}</el-tag>
            <span class="muted-text">当前用户：{{ currentIdentity.username }}</span>
            <span class="muted-text">姓名：{{ currentIdentity.realName || '未填写' }}</span>
          </div>
          <p class="topbar__hint">{{ roleFocus }}</p>
        </div>
        <div class="topbar__actions">
          <el-button plain @click="handleLogout">退出登录</el-button>
        </div>
      </header>
      <main class="content-area">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useIdentityStore } from '../utils/identity'

const props = defineProps({
  navItems: {
    type: Array,
    default: () => []
  },
  layoutTitle: {
    type: String,
    required: true
  }
})

const route = useRoute()
const router = useRouter()
const { currentIdentity, clearIdentity } = useIdentityStore()

const visibleNavItems = computed(() => {
  return props.navItems.filter((item) => !item.roles || item.roles.includes(currentIdentity.value.roleCode))
})

const currentNavLabel = computed(() => {
  return visibleNavItems.value.find((item) => route.path.startsWith(item.path))?.label || props.layoutTitle
})

const roleFocusMap = {
  STUDENT: '当前主页聚焦学生求职路径，可直接展示简历、岗位、投递与就业登记联动。',
  ENTERPRISE: '当前主页聚焦企业发岗与处理投递，可快速演示岗位状态和候选人跟进。',
  EMPLOYMENT_TEACHER: '当前主页聚焦审核与统计，可直接展示就业审核、公告和数据概览。',
  COUNSELOR: '当前主页聚焦学生就业跟踪与审核协同，可快速查看统计和审核流程。',
  ADMIN: '当前主页聚焦全局管理，可直接展示用户、公告、企业审核和统计数据。'
}

const roleFocus = computed(() => {
  return roleFocusMap[currentIdentity.value.roleCode] || '当前页面已进入对应业务工作台。'
})

function handleLogout() {
  clearIdentity()
  router.replace('/login')
}
</script>

<style scoped>
.role-layout {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 260px 1fr;
}

.role-layout__aside {
  padding: 24px 16px;
  border-right: 1px solid var(--app-border);
  background: linear-gradient(180deg, #0e3f43, #145c60);
  color: #fff;
}

.brand-block {
  padding: 12px 12px 20px;
}

.brand-block__eyebrow {
  margin: 0 0 8px;
  opacity: 0.72;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.12em;
}

h1 {
  margin: 0;
  font-size: 24px;
  line-height: 1.3;
}

.brand-block__role {
  margin: 12px 0 0;
  color: #c7ecec;
}

.nav-menu {
  border-right: none;
  background: transparent;
}

:deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.88);
  border-radius: 12px;
  margin-bottom: 6px;
}

:deep(.el-menu-item.is-active) {
  color: #0e3f43;
  background: #fff;
}

.role-layout__main {
  padding: 24px;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 18px 20px;
  margin-bottom: 20px;
}

.topbar__title {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 4px;
}

.topbar__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.topbar__hint {
  margin: 10px 0 0;
  color: var(--app-text-muted);
  max-width: 860px;
}

.content-area {
  display: grid;
  gap: 16px;
}

@media (max-width: 1080px) {
  .role-layout {
    grid-template-columns: 1fr;
  }

  .role-layout__aside {
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.16);
  }
}

@media (max-width: 768px) {
  .role-layout__main {
    padding: 16px;
  }

  .topbar {
    align-items: flex-start;
    flex-direction: column;
  }

  .topbar__actions {
    width: 100%;
  }
}
</style>
