<template>
  <div class="auth-shell">
    <section class="auth-shell__hero">
      <div>
        <p class="auth-shell__eyebrow">Create Account</p>
        <h1 class="auth-shell__title">注册业务账号</h1>
        <p class="auth-shell__subtitle">
          学生可直接创建学生端账号，企业可提交企业注册信息并进入待审核状态，后续仍沿用当前轻量角色机制。
        </p>
      </div>
      <div class="auth-shell__points">
        <div class="auth-shell__point">
          <strong>学生注册</strong>
          <span>创建账号后即可登录学生端，继续维护档案、简历与求职进展。</span>
        </div>
        <div class="auth-shell__point">
          <strong>企业注册</strong>
          <span>注册后生成企业账号与企业档案，默认进入待审核状态，便于后续管理。</span>
        </div>
      </div>
    </section>
    <section class="auth-shell__panel">
      <div class="auth-card page-card">
        <h2 class="auth-card__title">账号注册</h2>
        <p class="auth-card__desc">支持学生与企业注册，提交成功后返回登录页。</p>

        <el-tabs v-model="activeTab" stretch>
          <el-tab-pane label="学生注册" name="student">
            <el-form ref="studentFormRef" :model="studentForm" :rules="studentRules" label-position="top">
              <div class="register-grid">
                <el-form-item label="用户名" prop="username"><el-input v-model="studentForm.username" /></el-form-item>
                <el-form-item label="密码" prop="password"><el-input v-model="studentForm.password" type="password" show-password /></el-form-item>
                <el-form-item label="真实姓名" prop="realName"><el-input v-model="studentForm.realName" /></el-form-item>
                <el-form-item label="手机号" prop="phone"><el-input v-model="studentForm.phone" /></el-form-item>
                <el-form-item label="邮箱" prop="email"><el-input v-model="studentForm.email" /></el-form-item>
                <el-form-item label="学号" prop="studentNo"><el-input v-model="studentForm.studentNo" /></el-form-item>
                <el-form-item label="学院" prop="collegeName"><el-input v-model="studentForm.collegeName" /></el-form-item>
                <el-form-item label="专业" prop="majorName"><el-input v-model="studentForm.majorName" /></el-form-item>
                <el-form-item label="班级" prop="className"><el-input v-model="studentForm.className" /></el-form-item>
                <el-form-item label="学历层次" prop="educationLevel"><el-input v-model="studentForm.educationLevel" placeholder="如：本科" /></el-form-item>
                <el-form-item label="毕业年份" prop="graduationYear"><el-input-number v-model="studentForm.graduationYear" :min="2000" :max="2100" class="register-number" /></el-form-item>
              </div>
              <el-button type="primary" class="register-button" :loading="studentSubmitting" @click="submitStudent">
                提交学生注册
              </el-button>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="企业注册" name="enterprise">
            <el-form ref="enterpriseFormRef" :model="enterpriseForm" :rules="enterpriseRules" label-position="top">
              <div class="register-grid">
                <el-form-item label="用户名" prop="username"><el-input v-model="enterpriseForm.username" /></el-form-item>
                <el-form-item label="密码" prop="password"><el-input v-model="enterpriseForm.password" type="password" show-password /></el-form-item>
                <el-form-item label="企业名称" prop="enterpriseName"><el-input v-model="enterpriseForm.enterpriseName" /></el-form-item>
                <el-form-item label="联系人" prop="contactName"><el-input v-model="enterpriseForm.contactName" /></el-form-item>
                <el-form-item label="联系电话" prop="contactPhone"><el-input v-model="enterpriseForm.contactPhone" /></el-form-item>
                <el-form-item label="联系邮箱" prop="contactEmail"><el-input v-model="enterpriseForm.contactEmail" /></el-form-item>
                <el-form-item label="统一社会信用代码" prop="creditCode" class="register-span-full">
                  <el-input v-model="enterpriseForm.creditCode" />
                </el-form-item>
              </div>
              <el-button type="primary" class="register-button" :loading="enterpriseSubmitting" @click="submitEnterprise">
                提交企业注册
              </el-button>
            </el-form>
          </el-tab-pane>
        </el-tabs>

        <div class="auth-card__footer">
          已有账号？<RouterLink to="/login">返回登录</RouterLink>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerEnterprise, registerStudent } from '../../api/auth'
import { notifySuccess } from '../../utils/message'

const router = useRouter()
const activeTab = ref('student')
const studentFormRef = ref()
const enterpriseFormRef = ref()
const studentSubmitting = ref(false)
const enterpriseSubmitting = ref(false)

const studentForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  studentNo: '',
  collegeName: '',
  majorName: '',
  className: '',
  educationLevel: '本科',
  graduationYear: new Date().getFullYear()
})

const enterpriseForm = reactive({
  username: '',
  password: '',
  enterpriseName: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  creditCode: ''
})

const phoneRule = { pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }
const emailRule = { type: 'email', message: '请输入正确的邮箱', trigger: ['blur', 'change'] }

const studentRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, phoneRule],
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }, emailRule],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  collegeName: [{ required: true, message: '请输入学院', trigger: 'blur' }],
  majorName: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  educationLevel: [{ required: true, message: '请输入学历层次', trigger: 'blur' }],
  graduationYear: [{ required: true, message: '请输入毕业年份', trigger: 'change' }]
}

const enterpriseRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }, phoneRule],
  contactEmail: [{ required: true, message: '请输入联系邮箱', trigger: 'blur' }, emailRule],
  creditCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }]
}

async function submitStudent() {
  const valid = await studentFormRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  studentSubmitting.value = true
  try {
    await registerStudent(studentForm)
    notifySuccess('学生注册成功，请登录')
    router.replace('/login')
  } finally {
    studentSubmitting.value = false
  }
}

async function submitEnterprise() {
  const valid = await enterpriseFormRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  enterpriseSubmitting.value = true
  try {
    await registerEnterprise(enterpriseForm)
    notifySuccess('企业注册成功，请等待审核后登录')
    router.replace('/login')
  } finally {
    enterpriseSubmitting.value = false
  }
}
</script>

<style scoped>
.register-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 0 14px;
}

.register-button {
  width: 100%;
  height: 44px;
}

.register-number {
  width: 100%;
}

.register-span-full {
  grid-column: 1 / -1;
}

@media (max-width: 720px) {
  .register-grid {
    grid-template-columns: 1fr;
  }
}
</style>
