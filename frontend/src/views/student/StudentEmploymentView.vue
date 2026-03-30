<template>
  <div class="page-grid">
    <PageSection title="就业登记" description="查看、创建和维护当前学生就业记录。" :loading="pageLoading">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="就业状态" prop="employmentStatus">
              <el-select v-model="form.employmentStatus" class="w-full">
                <el-option
                  v-for="item in EMPLOYMENT_STATUS_OPTIONS"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就业类型" prop="employmentType">
              <el-input v-model="form.employmentType" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就业单位" prop="companyName">
              <el-input v-model="form.companyName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="jobTitle">
              <el-input v-model="form.jobTitle" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就业城市">
              <el-input v-model="form.workCity" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同开始日">
              <el-date-picker v-model="form.contractStartDate" value-format="YYYY-MM-DD" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资金额">
              <el-input-number v-model="form.salaryAmount" :min="0" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="填报来源">
              <el-input v-model="form.reportSource" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="form-actions">
          <el-button :loading="pageLoading" @click="loadRecord">重新加载</el-button>
          <el-button type="primary" :loading="recordSubmitting" @click="submitRecord">
            {{ exists ? '保存更新' : '创建登记' }}
          </el-button>
        </div>
      </el-form>
    </PageSection>

    <PageSection
      title="审核状态"
      description="就业记录审核结果和附件元数据维护。"
      :loading="pageLoading"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="审核状态">
          <StatusTag :text="record?.reviewStatusName || '未提交审核'" :status="record?.reviewStatus ?? ''" mode="review" />
        </el-descriptions-item>
        <el-descriptions-item label="审核备注">
          {{ record?.reviewRemark || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <div class="attachment-actions">
        <el-button type="primary" plain @click="attachmentDialogVisible = true">新增附件元数据</el-button>
      </div>

      <el-table :data="attachments" border empty-text="暂无附件元数据，请先新增一条">
        <el-table-column prop="fileName" label="文件名称" min-width="180" />
        <el-table-column prop="fileType" label="文件类型" width="120" />
        <el-table-column prop="fileUrl" label="文件地址" min-width="220" />
        <el-table-column prop="remark" label="备注" min-width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="danger" @click="removeAttachment(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </PageSection>

    <el-dialog v-model="attachmentDialogVisible" title="新增附件元数据" width="520px">
      <el-form ref="attachmentFormRef" :model="attachmentForm" :rules="attachmentRules" label-width="90px">
        <el-form-item label="文件名称" prop="fileName">
          <el-input v-model="attachmentForm.fileName" />
        </el-form-item>
        <el-form-item label="文件地址" prop="fileUrl">
          <el-input v-model="attachmentForm.fileUrl" />
        </el-form-item>
        <el-form-item label="文件类型" prop="fileType">
          <el-input v-model="attachmentForm.fileType" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="attachmentForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="attachmentDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="attachmentSubmitting" @click="submitAttachment">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import {
  createCurrentEmploymentRecord,
  getCurrentEmploymentRecord,
  updateCurrentEmploymentRecord
} from '../../api/employmentRecord'
import {
  createEmploymentAttachment,
  deleteEmploymentAttachment,
  listEmploymentAttachments
} from '../../api/employmentAttachment'
import { EMPLOYMENT_STATUS_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const formRef = ref()
const attachmentFormRef = ref()
const exists = ref(false)
const record = ref(null)
const attachments = ref([])
const attachmentDialogVisible = ref(false)
const pageLoading = ref(false)
const recordSubmitting = ref(false)
const attachmentSubmitting = ref(false)

const form = reactive({
  employmentStatus: 3,
  employmentType: '',
  companyName: '',
  jobTitle: '',
  workCity: '',
  contractStartDate: '',
  salaryAmount: null,
  reportSource: '',
  jobPostId: null,
  enterpriseId: null
})

const attachmentForm = reactive({
  fileName: '',
  fileUrl: '',
  fileType: '',
  remark: ''
})

const rules = {
  employmentStatus: [{ required: true, message: '请选择就业状态', trigger: 'change' }],
  employmentType: [{ required: true, message: '请输入就业类型', trigger: 'blur' }],
  companyName: [{ required: true, message: '请输入就业单位', trigger: 'blur' }],
  jobTitle: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }]
}

const attachmentRules = {
  fileName: [{ required: true, message: '请输入文件名称', trigger: 'blur' }],
  fileUrl: [{ required: true, message: '请输入文件地址', trigger: 'blur' }],
  fileType: [{ required: true, message: '请输入文件类型', trigger: 'blur' }]
}

function fillForm(data = {}) {
  Object.assign(form, {
    employmentStatus: data.employmentStatus ?? 3,
    employmentType: data.employmentType || '',
    companyName: data.companyName || '',
    jobTitle: data.jobTitle || '',
    workCity: data.workCity || '',
    contractStartDate: data.contractStartDate || '',
    salaryAmount: data.salaryAmount || null,
    reportSource: data.reportSource || '',
    jobPostId: data.jobPostId || null,
    enterpriseId: data.enterpriseId || null
  })
}

async function loadRecord() {
  pageLoading.value = true

  try {
    try {
      const data = await getCurrentEmploymentRecord()
      record.value = data
      fillForm(data)
      exists.value = true
    } catch {
      exists.value = false
      record.value = null
    }

    attachments.value = await listEmploymentAttachments().catch(() => [])
  } finally {
    pageLoading.value = false
  }
}

async function submitRecord() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  recordSubmitting.value = true

  try {
    const action = exists.value ? updateCurrentEmploymentRecord : createCurrentEmploymentRecord
    record.value = await action({ ...form })
    exists.value = true
    notifySuccess('就业登记已保存')
  } finally {
    recordSubmitting.value = false
  }
}

async function submitAttachment() {
  const valid = await attachmentFormRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  attachmentSubmitting.value = true

  try {
    await createEmploymentAttachment({ ...attachmentForm })
    attachmentDialogVisible.value = false
    Object.assign(attachmentForm, { fileName: '', fileUrl: '', fileType: '', remark: '' })
    attachments.value = await listEmploymentAttachments()
    notifySuccess('附件元数据已保存')
  } finally {
    attachmentSubmitting.value = false
  }
}

async function removeAttachment(id) {
  await ElMessageBox.confirm('确认删除该附件元数据吗？', '提示', { type: 'warning' })
  await deleteEmploymentAttachment(id)
  attachments.value = await listEmploymentAttachments()
  notifySuccess('附件元数据已删除')
}

onMounted(loadRecord)
</script>

<style scoped>
.w-full {
  width: 100%;
}

.attachment-actions {
  margin: 16px 0;
}
</style>
