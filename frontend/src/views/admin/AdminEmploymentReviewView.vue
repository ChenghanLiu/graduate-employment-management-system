<template>
  <PageSection
    title="就业审核"
    description="查看就业登记审核列表、详情并完成审核。"
    :loading="loading"
  >
    <el-table :data="records" border empty-text="当前没有待处理的就业记录">
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="companyName" label="就业单位" min-width="180" />
      <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
      <el-table-column label="就业状态" width="120">
        <template #default="{ row }">
          <StatusTag :text="row.employmentStatusName" :status="row.employmentStatus" />
        </template>
      </el-table-column>
      <el-table-column label="审核状态" width="120">
        <template #default="{ row }">
          <StatusTag :text="row.reviewStatusName" :status="row.reviewStatus" mode="review" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="viewDetail(row.id)">查看详情</el-button>
          <el-button link type="success" @click="openReview(row.id)">审核</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="detailVisible" title="就业登记详情" size="540px">
      <el-descriptions v-if="detail" :column="1" border>
        <el-descriptions-item label="学号">{{ detail.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="就业单位">{{ detail.companyName }}</el-descriptions-item>
        <el-descriptions-item label="岗位名称">{{ detail.jobTitle }}</el-descriptions-item>
        <el-descriptions-item label="就业类型">{{ detail.employmentType }}</el-descriptions-item>
        <el-descriptions-item label="就业状态">
          <StatusTag :text="detail.employmentStatusName" :status="detail.employmentStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <StatusTag :text="detail.reviewStatusName" :status="detail.reviewStatus" mode="review" />
        </el-descriptions-item>
        <el-descriptions-item label="审核备注">{{ detail.reviewRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <el-dialog v-model="reviewDialogVisible" title="审核就业登记" width="520px">
      <el-form ref="reviewFormRef" :model="reviewForm" :rules="reviewRules" label-width="90px">
        <el-form-item label="审核状态" prop="reviewStatus">
          <el-select v-model="reviewForm.reviewStatus" class="w-full">
            <el-option v-for="item in REVIEW_STATUS_OPTIONS.slice(1)" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核备注" prop="reviewRemark">
          <el-input v-model="reviewForm.reviewRemark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitReview">提交审核</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { getEmploymentRecordDetail, listEmploymentReviewRecords, reviewEmploymentRecord } from '../../api/employmentRecord'
import { REVIEW_STATUS_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const records = ref([])
const detailVisible = ref(false)
const reviewDialogVisible = ref(false)
const detail = ref(null)
const reviewFormRef = ref()
const loading = ref(false)
const submitting = ref(false)
const reviewForm = reactive({
  id: null,
  reviewStatus: 1,
  reviewRemark: ''
})

const reviewRules = {
  reviewStatus: [{ required: true, message: '请选择审核状态', trigger: 'change' }],
  reviewRemark: [{ required: true, message: '请输入审核备注', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true

  try {
    records.value = await listEmploymentReviewRecords()
  } finally {
    loading.value = false
  }
}

async function viewDetail(id) {
  detail.value = await getEmploymentRecordDetail(id)
  detailVisible.value = true
}

async function openReview(id) {
  detail.value = await getEmploymentRecordDetail(id)
  reviewForm.id = id
  reviewForm.reviewStatus = detail.value.reviewStatus === 2 ? 2 : 1
  reviewForm.reviewRemark = detail.value.reviewRemark || ''
  reviewDialogVisible.value = true
}

async function submitReview() {
  const valid = await reviewFormRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  await ElMessageBox.confirm('确认提交当前就业审核结果吗？', '审核确认', { type: 'warning' })
  submitting.value = true

  try {
    await reviewEmploymentRecord(reviewForm.id, {
      reviewStatus: reviewForm.reviewStatus,
      reviewRemark: reviewForm.reviewRemark
    })
    reviewDialogVisible.value = false
    records.value = await listEmploymentReviewRecords()
    notifySuccess('就业审核已提交')
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
