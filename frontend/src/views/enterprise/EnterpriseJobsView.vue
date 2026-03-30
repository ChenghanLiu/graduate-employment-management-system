<template>
  <PageSection
    title="岗位管理"
    description="维护企业岗位列表、岗位信息和岗位状态，列表补充了答辩展示常用字段。"
    :loading="loading"
  >
    <template #extra>
      <el-button type="primary" @click="openDialog()">新增岗位</el-button>
    </template>
    <el-table :data="jobs" border empty-text="当前还没有岗位，请先创建一个岗位用于演示发岗流程">
      <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
      <el-table-column prop="jobCategory" label="岗位类别" width="140" />
      <el-table-column prop="workCity" label="工作城市" width="120" />
      <el-table-column prop="salaryRemark" label="薪资说明" width="140" />
      <el-table-column prop="hiringCount" label="招聘人数" width="100" />
      <el-table-column prop="deadline" label="截止日期" width="130" />
      <el-table-column label="岗位说明" min-width="220">
        <template #default="{ row }">{{ summarize(row.jobDescription) }}</template>
      </el-table-column>
      <el-table-column label="岗位状态" width="120">
        <template #default="{ row }">
          <StatusTag :text="row.postStatusName" :status="row.postStatus" mode="job" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-dropdown @command="(value) => changeStatus(row, value)">
            <span class="el-dropdown-link link-text">更新状态</span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item
                  v-for="item in JOB_POST_STATUS_OPTIONS"
                  :key="item.value"
                  :command="item.value"
                >
                  {{ item.label }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑岗位' : '新增岗位'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="岗位名称" prop="jobTitle">
              <el-input v-model="form.jobTitle" placeholder="例如：Java后端开发工程师" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位类别" prop="jobCategory">
              <el-input v-model="form.jobCategory" placeholder="例如：技术研发 / 数据分析" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工作城市" prop="workCity">
              <el-input v-model="form.workCity" placeholder="例如：上海" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学历要求">
              <el-input v-model="form.educationRequirement" placeholder="例如：本科 / 硕士优先" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低薪资">
              <el-input-number v-model="form.salaryMin" :min="0" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最高薪资">
              <el-input-number v-model="form.salaryMax" :min="0" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资说明">
              <el-input v-model="form.salaryRemark" placeholder="例如：12k-18k·14薪" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="招聘人数" prop="hiringCount">
              <el-input-number v-model="form.hiringCount" :min="1" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期" prop="deadline">
              <el-date-picker v-model="form.deadline" value-format="YYYY-MM-DD" class="w-full" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="岗位描述" prop="jobDescription">
              <el-input v-model="form.jobDescription" type="textarea" :rows="4" placeholder="建议填写岗位职责、技术要求和培养方向，便于答辩时展示岗位信息完整性。" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitJob">保存</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { createJobPost, listMyJobPosts, updateJobPost, updateJobPostStatus } from '../../api/jobPost'
import { JOB_POST_STATUS_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const jobs = ref([])
const dialogVisible = ref(false)
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)
const form = reactive({
  id: null,
  jobTitle: '',
  jobCategory: '',
  workCity: '',
  educationRequirement: '',
  salaryMin: 0,
  salaryMax: 0,
  salaryRemark: '',
  hiringCount: 1,
  jobDescription: '',
  deadline: ''
})

const rules = {
  jobTitle: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  jobCategory: [{ required: true, message: '请输入岗位类别', trigger: 'blur' }],
  workCity: [{ required: true, message: '请输入工作城市', trigger: 'blur' }],
  hiringCount: [{ required: true, message: '请输入招聘人数', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择截止日期', trigger: 'change' }],
  jobDescription: [{ required: true, message: '请输入岗位描述', trigger: 'blur' }]
}

function resetForm(data = {}) {
  Object.assign(form, {
    id: data.id || null,
    jobTitle: data.jobTitle || '',
    jobCategory: data.jobCategory || '',
    workCity: data.workCity || '',
    educationRequirement: data.educationRequirement || '',
    salaryMin: data.salaryMin || 0,
    salaryMax: data.salaryMax || 0,
    salaryRemark: data.salaryRemark || '',
    hiringCount: data.hiringCount || 1,
    jobDescription: data.jobDescription || '',
    deadline: data.deadline || ''
  })
}

async function loadData() {
  loading.value = true

  try {
    jobs.value = await listMyJobPosts()
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  resetForm(row)
  dialogVisible.value = true
}

function summarize(text) {
  if (!text) {
    return '-'
  }

  return text.length > 32 ? `${text.slice(0, 32)}...` : text
}

async function submitJob() {
  const valid = await formRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  submitting.value = true

  const payload = { ...form }
  delete payload.id

  try {
    if (form.id) {
      await updateJobPost(form.id, payload)
    } else {
      await createJobPost(payload)
    }

    dialogVisible.value = false
    jobs.value = await listMyJobPosts()
    notifySuccess('岗位信息已保存')
  } finally {
    submitting.value = false
  }
}

async function changeStatus(row, status) {
  await ElMessageBox.confirm(`确认将岗位“${row.jobTitle}”状态更新为“${JOB_POST_STATUS_OPTIONS.find((item) => item.value === status)?.label || status}”吗？`, '状态确认', {
    type: 'warning'
  })
  await updateJobPostStatus(row.id, { postStatus: status })
  jobs.value = await listMyJobPosts()
  notifySuccess('岗位状态已更新')
}

onMounted(loadData)
</script>

<style scoped>
.w-full {
  width: 100%;
}

.link-text {
  color: var(--el-color-primary);
  cursor: pointer;
}
</style>
