<template>
  <PageSection title="岗位详情" description="查看岗位详细信息并进行投递。" :loading="loading" :empty="!job" empty-description="未找到岗位详情">
    <el-descriptions v-if="job" :column="2" border>
      <el-descriptions-item label="岗位名称">{{ job.jobTitle }}</el-descriptions-item>
      <el-descriptions-item label="企业名称">{{ job.enterpriseName }}</el-descriptions-item>
      <el-descriptions-item label="工作城市">{{ job.workCity }}</el-descriptions-item>
      <el-descriptions-item label="学历要求">{{ job.educationRequirement || '-' }}</el-descriptions-item>
      <el-descriptions-item label="薪资区间">
        {{ job.salaryMin || '-' }} - {{ job.salaryMax || '-' }} {{ job.salaryRemark || '' }}
      </el-descriptions-item>
      <el-descriptions-item label="截止日期">{{ job.deadline || '-' }}</el-descriptions-item>
      <el-descriptions-item label="岗位状态">
        <StatusTag :text="job.postStatusName" :status="job.postStatus" mode="job" />
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatDateTime(job.createTime) }}</el-descriptions-item>
      <el-descriptions-item label="岗位描述" :span="2">{{ job.jobDescription || '-' }}</el-descriptions-item>
    </el-descriptions>
    <div class="form-actions detail-actions">
      <el-button @click="$router.back()">返回列表</el-button>
      <el-button type="primary" :loading="submitting" @click="apply">立即投递</el-button>
    </div>
  </PageSection>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useRoute } from 'vue-router'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { getJobPostDetail } from '../../api/jobPost'
import { createJobApplication } from '../../api/jobApplication'
import { notifySuccess } from '../../utils/message'
import { formatDateTime } from '../../utils/format'

const route = useRoute()
const job = ref(null)
const loading = ref(false)
const submitting = ref(false)

async function loadData() {
  loading.value = true

  try {
    job.value = await getJobPostDetail(route.params.id)
  } finally {
    loading.value = false
  }
}

async function apply() {
  await ElMessageBox.confirm(`确认投递岗位“${job.value.jobTitle}”吗？`, '投递确认', { type: 'warning' })
  submitting.value = true

  try {
    await createJobApplication({
      jobPostId: job.value.id,
      remark: '来自岗位详情页的投递'
    })
    notifySuccess('岗位投递已提交')
  } finally {
    submitting.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.detail-actions {
  margin-top: 16px;
}
</style>
