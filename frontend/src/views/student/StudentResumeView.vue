<template>
  <div class="page-grid">
    <PageSection title="简历主信息" description="维护当前学生的主简历。" :loading="pageLoading">
      <el-form ref="resumeFormRef" :model="resumeForm" :rules="resumeRules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="简历名称" prop="resumeName">
              <el-input v-model="resumeForm.resumeName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="求职意向" prop="jobIntention">
              <el-input v-model="resumeForm.jobIntention" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望城市">
              <el-input v-model="resumeForm.expectedCity" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望薪资">
              <el-input v-model="resumeForm.expectedSalary" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="自我评价">
              <el-input v-model="resumeForm.selfEvaluation" type="textarea" :rows="4" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="form-actions">
          <el-button type="primary" :loading="resumeSubmitting" @click="submitResume">
            {{ resumeExists ? '保存简历' : '创建简历' }}
          </el-button>
        </div>
      </el-form>
    </PageSection>

    <PageSection
      title="教育经历"
      description="教育经历列表维护。"
      :loading="pageLoading"
      :empty="!educations.length"
      empty-description="暂无教育经历，请先新增一条记录"
    >
      <template #extra>
        <el-button type="primary" plain @click="openEducationDialog()">新增教育经历</el-button>
      </template>
      <el-table :data="educations" border>
        <el-table-column prop="schoolName" label="学校" min-width="160" />
        <el-table-column prop="majorName" label="专业" min-width="160" />
        <el-table-column prop="educationLevel" label="学历" width="120" />
        <el-table-column label="起止时间" min-width="180">
          <template #default="{ row }">{{ row.startDate || '-' }} 至 {{ row.endDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEducationDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="removeEducation(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </PageSection>

    <PageSection
      title="项目经历"
      description="项目经验维护。"
      :loading="pageLoading"
      :empty="!projects.length"
      empty-description="暂无项目经历，请补充项目经验"
    >
      <template #extra>
        <el-button type="primary" plain @click="openProjectDialog()">新增项目</el-button>
      </template>
      <el-table :data="projects" border>
        <el-table-column prop="projectName" label="项目名称" min-width="180" />
        <el-table-column prop="roleName" label="角色" width="140" />
        <el-table-column label="时间" min-width="180">
          <template #default="{ row }">{{ row.startDate || '-' }} 至 {{ row.endDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openProjectDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="removeProject(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </PageSection>

    <PageSection
      title="技能清单"
      description="技能和熟练度维护。"
      :loading="pageLoading"
      :empty="!skills.length"
      empty-description="暂无技能信息，请添加技能标签"
    >
      <template #extra>
        <el-button type="primary" plain @click="openSkillDialog()">新增技能</el-button>
      </template>
      <el-table :data="skills" border>
        <el-table-column prop="skillName" label="技能" min-width="180" />
        <el-table-column prop="skillLevel" label="熟练度" width="120" />
        <el-table-column prop="description" label="说明" min-width="200" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button link type="primary" @click="openSkillDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="removeSkill(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </PageSection>

    <el-dialog v-model="educationDialogVisible" :title="educationForm.id ? '编辑教育经历' : '新增教育经历'" width="640px">
      <el-form ref="educationFormRef" :model="educationForm" :rules="educationRules" label-width="90px">
        <el-form-item label="学校" prop="schoolName">
          <el-input v-model="educationForm.schoolName" />
        </el-form-item>
        <el-form-item label="专业" prop="majorName">
          <el-input v-model="educationForm.majorName" />
        </el-form-item>
        <el-form-item label="学历" prop="educationLevel">
          <el-select v-model="educationForm.educationLevel" class="w-full">
            <el-option v-for="item in EDUCATION_LEVEL_OPTIONS" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="起止日期" prop="startDate">
          <el-date-picker
            v-model="educationDates"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="educationForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="educationDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="educationSubmitting" @click="submitEducation">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="projectDialogVisible" :title="projectForm.id ? '编辑项目' : '新增项目'" width="640px">
      <el-form ref="projectFormRef" :model="projectForm" :rules="projectRules" label-width="90px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="projectForm.projectName" />
        </el-form-item>
        <el-form-item label="角色" prop="roleName">
          <el-input v-model="projectForm.roleName" />
        </el-form-item>
        <el-form-item label="起止日期" prop="startDate">
          <el-date-picker
            v-model="projectDates"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="projectForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="projectSubmitting" @click="submitProject">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="skillDialogVisible" :title="skillForm.id ? '编辑技能' : '新增技能'" width="560px">
      <el-form ref="skillFormRef" :model="skillForm" :rules="skillRules" label-width="90px">
        <el-form-item label="技能名称" prop="skillName">
          <el-input v-model="skillForm.skillName" />
        </el-form-item>
        <el-form-item label="熟练度" prop="skillLevel">
          <el-select v-model="skillForm.skillLevel" class="w-full">
            <el-option
              v-for="item in SKILL_LEVEL_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="skillForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="skillDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="skillSubmitting" @click="submitSkill">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import {
  createCurrentResume,
  createResumeEducation,
  createResumeProject,
  createResumeSkill,
  deleteResumeEducation,
  deleteResumeProject,
  deleteResumeSkill,
  getCurrentResume,
  listResumeEducations,
  listResumeProjects,
  listResumeSkills,
  updateCurrentResume,
  updateResumeEducation,
  updateResumeProject,
  updateResumeSkill
} from '../../api/resume'
import { EDUCATION_LEVEL_OPTIONS, SKILL_LEVEL_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const resumeFormRef = ref()
const educationFormRef = ref()
const projectFormRef = ref()
const skillFormRef = ref()
const resumeExists = ref(false)
const educations = ref([])
const projects = ref([])
const skills = ref([])
const pageLoading = ref(false)
const resumeSubmitting = ref(false)
const educationSubmitting = ref(false)
const projectSubmitting = ref(false)
const skillSubmitting = ref(false)

const resumeForm = reactive({
  resumeName: '',
  jobIntention: '',
  expectedCity: '',
  expectedSalary: '',
  selfEvaluation: ''
})

const resumeRules = {
  resumeName: [{ required: true, message: '请输入简历名称', trigger: 'blur' }],
  jobIntention: [{ required: true, message: '请输入求职意向', trigger: 'blur' }]
}

const educationRules = {
  schoolName: [{ required: true, message: '请输入学校名称', trigger: 'blur' }],
  majorName: [{ required: true, message: '请输入专业名称', trigger: 'blur' }],
  educationLevel: [{ required: true, message: '请选择学历', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择起止日期', trigger: 'change' }]
}

const projectRules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入承担角色', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择项目时间', trigger: 'change' }]
}

const skillRules = {
  skillName: [{ required: true, message: '请输入技能名称', trigger: 'blur' }],
  skillLevel: [{ required: true, message: '请选择熟练度', trigger: 'change' }]
}

const educationDialogVisible = ref(false)
const projectDialogVisible = ref(false)
const skillDialogVisible = ref(false)

const educationForm = reactive({
  id: null,
  schoolName: '',
  majorName: '',
  educationLevel: '本科',
  startDate: '',
  endDate: '',
  description: '',
  sortOrder: 0
})

const projectForm = reactive({
  id: null,
  projectName: '',
  roleName: '',
  startDate: '',
  endDate: '',
  description: '',
  sortOrder: 0
})

const skillForm = reactive({
  id: null,
  skillName: '',
  skillLevel: 2,
  description: '',
  sortOrder: 0
})

const educationDates = computed({
  get: () => (educationForm.startDate || educationForm.endDate ? [educationForm.startDate, educationForm.endDate] : []),
  set: (value) => {
    educationForm.startDate = value?.[0] || ''
    educationForm.endDate = value?.[1] || ''
  }
})

const projectDates = computed({
  get: () => (projectForm.startDate || projectForm.endDate ? [projectForm.startDate, projectForm.endDate] : []),
  set: (value) => {
    projectForm.startDate = value?.[0] || ''
    projectForm.endDate = value?.[1] || ''
  }
})

function resetResumeForm(data = {}) {
  Object.assign(resumeForm, {
    resumeName: data.resumeName || '',
    jobIntention: data.jobIntention || '',
    expectedCity: data.expectedCity || '',
    expectedSalary: data.expectedSalary || '',
    selfEvaluation: data.selfEvaluation || ''
  })
}

function resetEducationForm(data = {}) {
  Object.assign(educationForm, {
    id: data.id || null,
    schoolName: data.schoolName || '',
    majorName: data.majorName || '',
    educationLevel: data.educationLevel || '本科',
    startDate: data.startDate || '',
    endDate: data.endDate || '',
    description: data.description || '',
    sortOrder: data.sortOrder || 0
  })
}

function resetProjectForm(data = {}) {
  Object.assign(projectForm, {
    id: data.id || null,
    projectName: data.projectName || '',
    roleName: data.roleName || '',
    startDate: data.startDate || '',
    endDate: data.endDate || '',
    description: data.description || '',
    sortOrder: data.sortOrder || 0
  })
}

function resetSkillForm(data = {}) {
  Object.assign(skillForm, {
    id: data.id || null,
    skillName: data.skillName || '',
    skillLevel: data.skillLevel || 2,
    description: data.description || '',
    sortOrder: data.sortOrder || 0
  })
}

async function loadData() {
  pageLoading.value = true

  try {
    try {
      const resume = await getCurrentResume()
      resetResumeForm(resume)
      resumeExists.value = true
    } catch {
      resumeExists.value = false
      resetResumeForm()
    }

    educations.value = await listResumeEducations().catch(() => [])
    projects.value = await listResumeProjects().catch(() => [])
    skills.value = await listResumeSkills().catch(() => [])
  } finally {
    pageLoading.value = false
  }
}

async function submitResume() {
  const valid = await resumeFormRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  resumeSubmitting.value = true

  try {
    const action = resumeExists.value ? updateCurrentResume : createCurrentResume
    await action({ ...resumeForm })
    resumeExists.value = true
    notifySuccess('简历主信息已保存')
  } finally {
    resumeSubmitting.value = false
  }
}

function openEducationDialog(row) {
  resetEducationForm(row)
  educationDialogVisible.value = true
}

function openProjectDialog(row) {
  resetProjectForm(row)
  projectDialogVisible.value = true
}

function openSkillDialog(row) {
  resetSkillForm(row)
  skillDialogVisible.value = true
}

async function submitEducation() {
  const valid = await educationFormRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  educationSubmitting.value = true

  const payload = { ...educationForm }
  delete payload.id

  try {
    if (educationForm.id) {
      await updateResumeEducation(educationForm.id, payload)
    } else {
      await createResumeEducation(payload)
    }

    educationDialogVisible.value = false
    educations.value = await listResumeEducations()
    notifySuccess('教育经历已保存')
  } finally {
    educationSubmitting.value = false
  }
}

async function submitProject() {
  const valid = await projectFormRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  projectSubmitting.value = true

  const payload = { ...projectForm }
  delete payload.id

  try {
    if (projectForm.id) {
      await updateResumeProject(projectForm.id, payload)
    } else {
      await createResumeProject(payload)
    }

    projectDialogVisible.value = false
    projects.value = await listResumeProjects()
    notifySuccess('项目经历已保存')
  } finally {
    projectSubmitting.value = false
  }
}

async function submitSkill() {
  const valid = await skillFormRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  skillSubmitting.value = true

  const payload = { ...skillForm }
  delete payload.id

  try {
    if (skillForm.id) {
      await updateResumeSkill(skillForm.id, payload)
    } else {
      await createResumeSkill(payload)
    }

    skillDialogVisible.value = false
    skills.value = await listResumeSkills()
    notifySuccess('技能信息已保存')
  } finally {
    skillSubmitting.value = false
  }
}

async function removeEducation(id) {
  await ElMessageBox.confirm('确认删除该教育经历吗？', '提示', { type: 'warning' })
  await deleteResumeEducation(id)
  educations.value = await listResumeEducations()
  notifySuccess('教育经历已删除')
}

async function removeProject(id) {
  await ElMessageBox.confirm('确认删除该项目经历吗？', '提示', { type: 'warning' })
  await deleteResumeProject(id)
  projects.value = await listResumeProjects()
  notifySuccess('项目经历已删除')
}

async function removeSkill(id) {
  await ElMessageBox.confirm('确认删除该技能吗？', '提示', { type: 'warning' })
  await deleteResumeSkill(id)
  skills.value = await listResumeSkills()
  notifySuccess('技能已删除')
}

onMounted(loadData)
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
