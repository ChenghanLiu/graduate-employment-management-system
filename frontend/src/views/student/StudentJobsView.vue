<template>
  <PageSection
    title="岗位列表"
    description="浏览开放岗位、查看详情并发起投递。"
    :loading="loading"
  >
    <el-table :data="jobs" border empty-text="当前暂无开放岗位，企业端发布后会在此实时展示">
      <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
      <el-table-column prop="enterpriseName" label="企业" min-width="180" />
      <el-table-column prop="jobCategory" label="岗位类别" width="130" />
      <el-table-column prop="workCity" label="工作城市" width="120" />
      <el-table-column prop="educationRequirement" label="学历要求" width="120" />
      <el-table-column prop="salaryRemark" label="薪资说明" width="140" />
      <el-table-column label="岗位说明" min-width="240">
        <template #default="{ row }">{{ summarize(row.jobDescription) }}</template>
      </el-table-column>
      <el-table-column label="岗位状态" width="120">
        <template #default="{ row }">
          <StatusTag :text="row.postStatusName" :status="row.postStatus" mode="job" />
        </template>
      </el-table-column>
      <el-table-column prop="deadline" label="截止日期" width="140" />
      <el-table-column label="操作" width="220">
        <template #default="{ row }">
          <el-button link type="primary" @click="$router.push(`/student/jobs/${row.id}`)">查看详情</el-button>
          <el-button link type="success" @click="openApplyDialog(row)">投递岗位</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="applyDialogVisible" title="提交岗位投递" width="520px">
      <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="90px">
        <el-form-item label="岗位">
          <el-input :model-value="selectedJob?.jobTitle" disabled />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="applyForm.remark" type="textarea" :rows="4" placeholder="例如：可尽快到岗，已准备好项目作品与简历附件。" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitApplication">确认投递</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { listOpenJobPosts } from '../../api/jobPost'
import { createJobApplication } from '../../api/jobApplication'
import { notifySuccess } from '../../utils/message'

const jobs = ref([])
const selectedJob = ref(null)
const applyDialogVisible = ref(false)
const applyFormRef = ref()
const loading = ref(false)
const submitting = ref(false)
const applyForm = ref({
  remark: ''
})

const applyRules = {
  remark: [{ required: true, message: '请输入投递备注，便于演示展示', trigger: 'blur' }]
}

async function loadJobs() {
  loading.value = true

  try {
    jobs.value = await listOpenJobPosts()
  } finally {
    loading.value = false
  }
}

function openApplyDialog(row) {
  selectedJob.value = row
  applyForm.value.remark = `对“${row.jobTitle}”岗位感兴趣，期待进一步沟通。`
  applyDialogVisible.value = true
}

function summarize(text) {
  if (!text) {
    return '-'
  }

  return text.length > 34 ? `${text.slice(0, 34)}...` : text
}

async function submitApplication() {
  const valid = await applyFormRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  await ElMessageBox.confirm(`确认向“${selectedJob.value.jobTitle}”提交投递吗？`, '投递确认', { type: 'warning' })
  submitting.value = true

  try {
    await createJobApplication({
      jobPostId: selectedJob.value.id,
      remark: applyForm.value.remark
    })
    applyDialogVisible.value = false
    notifySuccess('岗位投递已提交')
  } finally {
    submitting.value = false
  }
}

onMounted(loadJobs)
</script>
