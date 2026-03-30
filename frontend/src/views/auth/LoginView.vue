<template>
  <div class="auth-shell">
    <section class="auth-shell__hero">
      <div>
        <p class="auth-shell__eyebrow">Graduate Employment Management</p>
        <h1 class="auth-shell__title">毕业生就业管理系统</h1>
        <p class="auth-shell__subtitle">
          面向学生、企业与校方管理的一体化就业服务平台，统一完成岗位发布、简历投递、就业登记与审核管理。
        </p>
      </div>
      <div class="auth-shell__points">
        <div class="auth-shell__point">
          <strong>学生端</strong>
          <span>维护档案与简历，查看岗位、跟踪投递与登记就业去向。</span>
        </div>
        <div class="auth-shell__point">
          <strong>企业端</strong>
          <span>维护企业资料、发布岗位、处理投递并查看审核结果。</span>
        </div>
        <div class="auth-shell__point">
          <strong>管理端</strong>
          <span>进行公告发布、企业审核、就业审核与全局统计分析。</span>
        </div>
      </div>
    </section>
    <section class="auth-shell__panel">
      <div class="auth-card page-card">
        <h2 class="auth-card__title">账号登录</h2>
        <p class="auth-card__desc">请输入用户名与密码，系统将自动进入对应角色首页。</p>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="handleLogin">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" class="login-button" @click="handleLogin">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="auth-card__hint">
          <div>登录说明：无需手动切换角色，系统将根据账号所属角色自动进入学生端、企业端或管理端。</div>
          <div class="muted-text">演示账号示例：`student01` / `enterprise01` / `teacher1` / `admin`</div>
        </div>
        <div class="auth-card__footer">
          没有账号？<RouterLink to="/register">立即注册</RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '../../api/auth'
import { useIdentityStore } from '../../utils/identity'
import { notifySuccess } from '../../utils/message'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const submitting = ref(false)
const { setIdentity } = useIdentityStore()

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  submitting.value = true
  try {
    const data = await login(form)
    setIdentity(data)
    notifySuccess('登录成功')
    router.replace(route.query.redirect || data.defaultPath || '/')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.login-button {
  width: 100%;
  height: 44px;
}
</style>
