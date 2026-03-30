<template>
  <PageSection
    title="投递管理"
    description="查看收到的岗位投递并更新投递状态。"
    :loading="loading"
  >
    <el-table :data="applications" border empty-text="当前还没有收到投递">
      <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="collegeName" label="学院" width="160" />
      <el-table-column prop="majorName" label="专业" width="160" />
      <el-table-column prop="resumeName" label="简历名称" width="160" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <StatusTag :text="row.applicationStatusName" :status="row.applicationStatus" mode="application" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">查看详情</el-button>
          <el-button link type="success" @click="openStatusDialog(row)">更新状态</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="detailVisible" title="投递详情" size="480px">
      <el-descriptions v-if="currentRow" :column="1" border>
        <el-descriptions-item label="岗位">{{ currentRow.jobTitle }}</el-descriptions-item>
        <el-descriptions-item label="企业">{{ currentRow.enterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="学生学号">{{ currentRow.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="学院 / 专业">{{ currentRow.collegeName }} / {{ currentRow.majorName }}</el-descriptions-item>
        <el-descriptions-item label="简历">{{ currentRow.resumeName }}</el-descriptions-item>
        <el-descriptions-item label="投递状态">
          <StatusTag :text="currentRow.applicationStatusName" :status="currentRow.applicationStatus" mode="application" />
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentRow.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <el-dialog v-model="statusDialogVisible" title="更新投递状态" width="520px">
      <el-form ref="statusFormRef" :model="statusForm" :rules="statusRules" label-width="90px">
        <el-form-item label="状态" prop="applicationStatus">
          <el-select v-model="statusForm.applicationStatus" class="w-full">
            <el-option
              v-for="item in APPLICATION_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="statusForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitStatus">保存</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { listReceivedJobApplications, updateJobApplicationStatus } from '../../api/jobApplication'
import { APPLICATION_STATUS_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const applications = ref([])
const detailVisible = ref(false)
const statusDialogVisible = ref(false)
const currentRow = ref(null)
const statusFormRef = ref()
const loading = ref(false)
const submitting = ref(false)
const statusForm = reactive({
  applicationStatus: 1,
  remark: ''
})

const statusRules = {
  applicationStatus: [{ required: true, message: '请选择投递状态', trigger: 'change' }]
}

async function loadData() {
  loading.value = true

  try {
    applications.value = await listReceivedJobApplications()
  } finally {
    loading.value = false
  }
}

function openDetail(row) {
  currentRow.value = row
  detailVisible.value = true
}

function openStatusDialog(row) {
  currentRow.value = row
  statusForm.applicationStatus = row.applicationStatus
  statusForm.remark = row.remark || ''
  statusDialogVisible.value = true
}

async function submitStatus() {
  const valid = await statusFormRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  await ElMessageBox.confirm(`确认将投递状态更新为“${APPLICATION_STATUS_OPTIONS.find((item) => item.value === statusForm.applicationStatus)?.label || statusForm.applicationStatus}”吗？`, '状态确认', {
    type: 'warning'
  })
  submitting.value = true

  try {
    await updateJobApplicationStatus(currentRow.value.id, { ...statusForm })
    statusDialogVisible.value = false
    applications.value = await listReceivedJobApplications()
    notifySuccess('投递状态已更新')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
