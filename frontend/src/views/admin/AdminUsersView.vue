<template>
  <PageSection
    title="用户管理"
    description="查看用户、编辑用户并集中分配角色。"
    :loading="loading"
  >
    <template #extra>
      <el-button type="primary" @click="openUserDialog()">新增用户</el-button>
    </template>
    <el-table :data="users" border empty-text="暂无用户数据">
      <el-table-column prop="username" label="用户名" width="160" />
      <el-table-column prop="realName" label="姓名" width="140" />
      <el-table-column prop="phone" label="手机号" width="150" />
      <el-table-column prop="email" label="邮箱" min-width="200" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <StatusTag :text="row.status === 1 ? '启用' : '禁用'" :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column label="角色" min-width="180">
        <template #default="{ row }">{{ row.roles.map((item) => item.roleName).join(' / ') }}</template>
      </el-table-column>
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button link type="primary" @click="openUserDialog(row)">编辑</el-button>
          <el-button link type="success" @click="openRoleDialog(row)">分配角色</el-button>
          <el-button link type="danger" @click="disableUser(row.id)">禁用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="userDialogVisible" :title="userForm.id ? '编辑用户' : '新增用户'" width="640px">
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="90px">
        <el-form-item label="用户名" prop="username"><el-input v-model="userForm.username" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="userForm.password" placeholder="留空则使用后端默认策略" /></el-form-item>
        <el-form-item label="姓名" prop="realName"><el-input v-model="userForm.realName" /></el-form-item>
        <el-form-item label="手机号" prop="phone"><el-input v-model="userForm.phone" /></el-form-item>
        <el-form-item label="邮箱" prop="email"><el-input v-model="userForm.email" /></el-form-item>
        <el-form-item label="状态"><el-switch v-model="userStatusSwitch" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="userSubmitting" @click="submitUser">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" title="分配角色" width="520px">
      <el-checkbox-group v-model="roleForm.roleIds">
        <el-checkbox v-for="item in roles" :key="item.id" :label="item.id">{{ item.roleName }}</el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleSubmitting" @click="submitRoles">保存</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import {
  assignAdminUserRoles,
  createAdminUser,
  disableAdminUser,
  listAdminRoles,
  listAdminUsers,
  updateAdminUser
} from '../../api/admin'
import { notifySuccess } from '../../utils/message'

const users = ref([])
const roles = ref([])
const userDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const userFormRef = ref()
const loading = ref(false)
const userSubmitting = ref(false)
const roleSubmitting = ref(false)
const userForm = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  status: 1
})
const roleForm = reactive({
  id: null,
  roleIds: []
})

const userRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    {
      validator: (_, value, callback) => {
        if (!userForm.id && !value) {
          callback(new Error('新增用户时请输入密码'))
          return
        }

        callback()
      },
      trigger: 'blur'
    }
  ]
}

const userStatusSwitch = computed({
  get: () => userForm.status === 1,
  set: (value) => {
    userForm.status = value ? 1 : 0
  }
})

async function loadData() {
  loading.value = true

  try {
    users.value = await listAdminUsers()
    roles.value = await listAdminRoles()
  } finally {
    loading.value = false
  }
}

function resetUserForm(row = {}) {
  Object.assign(userForm, {
    id: row.id || null,
    username: row.username || '',
    password: '',
    realName: row.realName || '',
    phone: row.phone || '',
    email: row.email || '',
    status: row.status ?? 1
  })
}

function openUserDialog(row) {
  resetUserForm(row)
  userDialogVisible.value = true
}

function openRoleDialog(row) {
  roleForm.id = row.id
  roleForm.roleIds = row.roles.map((item) => item.id)
  roleDialogVisible.value = true
}

async function submitUser() {
  const valid = await userFormRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  userSubmitting.value = true
  const payload = { ...userForm }
  delete payload.id

  try {
    if (userForm.id) {
      await updateAdminUser(userForm.id, payload)
    } else {
      await createAdminUser(payload)
    }

    userDialogVisible.value = false
    users.value = await listAdminUsers()
    notifySuccess('用户信息已保存')
  } finally {
    userSubmitting.value = false
  }
}

async function submitRoles() {
  roleSubmitting.value = true

  try {
    await assignAdminUserRoles(roleForm.id, { roleIds: roleForm.roleIds })
    roleDialogVisible.value = false
    users.value = await listAdminUsers()
    notifySuccess('用户角色已更新')
  } finally {
    roleSubmitting.value = false
  }
}

async function disableUser(id) {
  await ElMessageBox.confirm('确认禁用该用户吗？', '提示', { type: 'warning' })
  await disableAdminUser(id)
  users.value = await listAdminUsers()
  notifySuccess('用户已禁用')
}

onMounted(loadData)
</script>
