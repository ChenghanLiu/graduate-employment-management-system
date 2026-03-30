<template>
  <PageSection
    title="公告管理"
    description="查看、创建、编辑、上下线公告，列表保留了发布时间和内容摘要。"
    :loading="loading"
  >
    <template #extra>
      <el-button type="primary" @click="openDialog()">新建公告</el-button>
    </template>
    <el-table :data="announcements" border empty-text="暂无公告数据，可先新增一条用于演示公告发布流程">
      <el-table-column prop="title" label="标题" min-width="240" />
      <el-table-column prop="type" label="类型" width="140" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <StatusTag :text="row.statusName" :status="row.status" mode="announcement" />
        </template>
      </el-table-column>
      <el-table-column label="发布时间" width="180">
        <template #default="{ row }">{{ row.publishTime || row.createTime || '-' }}</template>
      </el-table-column>
      <el-table-column label="内容摘要" min-width="260">
        <template #default="{ row }">{{ summarize(row.content) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-dropdown @command="(value) => changeStatus(row, value)">
            <span class="el-dropdown-link link-text">切换状态</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="item in ANNOUNCEMENT_STATUS_OPTIONS"
                  :key="item.value"
                  :command="item.value"
                >
                  {{ item.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button link type="danger" @click="removeAnnouncement(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新建公告'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="例如：关于开展就业协议书集中审核的通知" /></el-form-item>
        <el-form-item label="类型" prop="type"><el-input v-model="form.type" placeholder="例如：就业通知 / 政策公告 / 双选会通知" /></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="8" placeholder="建议补充时间、对象、操作路径和提醒事项，便于演示公告详情页。" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import {
  createAdminAnnouncement,
  deleteAdminAnnouncement,
  listAdminAnnouncements,
  updateAdminAnnouncement,
  updateAdminAnnouncementStatus
} from '../../api/admin'
import { ANNOUNCEMENT_STATUS_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const announcements = ref([])
const dialogVisible = ref(false)
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)
const form = reactive({
  id: null,
  title: '',
  type: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  type: [{ required: true, message: '请输入公告类型', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true

  try {
    announcements.value = await listAdminAnnouncements()
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  Object.assign(form, {
    id: row?.id || null,
    title: row?.title || '',
    type: row?.type || '',
    content: row?.content || ''
  })
  dialogVisible.value = true
}

function summarize(text) {
  if (!text) {
    return '-'
  }

  return text.length > 36 ? `${text.slice(0, 36)}...` : text
}

async function submitForm() {
  const valid = await formRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  submitting.value = true
  const payload = { title: form.title, type: form.type, content: form.content }

  try {
    if (form.id) {
      await updateAdminAnnouncement(form.id, payload)
    } else {
      await createAdminAnnouncement(payload)
    }

    dialogVisible.value = false
    announcements.value = await listAdminAnnouncements()
    notifySuccess('公告已保存')
  } finally {
    submitting.value = false
  }
}

async function changeStatus(row, status) {
  await ElMessageBox.confirm(`确认将公告“${row.title}”状态切换为“${ANNOUNCEMENT_STATUS_OPTIONS.find((item) => item.value === status)?.label || status}”吗？`, '状态确认', {
    type: 'warning'
  })
  await updateAdminAnnouncementStatus(row.id, { status })
  announcements.value = await listAdminAnnouncements()
  notifySuccess('公告状态已更新')
}

async function removeAnnouncement(id) {
  await ElMessageBox.confirm('确认删除该公告吗？', '提示', { type: 'warning' })
  await deleteAdminAnnouncement(id)
  announcements.value = await listAdminAnnouncements()
  notifySuccess('公告已删除')
}

onMounted(loadData)
</script>

<style scoped>
.link-text {
  color: var(--el-color-primary);
  cursor: pointer;
}
</style>
