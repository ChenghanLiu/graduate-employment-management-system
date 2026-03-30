<template>
  <PageSection title="学生档案" description="查看并维护当前学生身份的基础档案信息。">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="学号" prop="studentNo">
            <el-input v-model="form.studentNo" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="性别" prop="gender">
            <el-select v-model="form.gender" class="w-full">
              <el-option v-for="item in GENDER_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学院" prop="collegeName">
            <el-input v-model="form.collegeName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="专业" prop="majorName">
            <el-input v-model="form.majorName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="班级" prop="className">
            <el-input v-model="form.className" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="学历层次" prop="educationLevel">
            <el-select v-model="form.educationLevel" class="w-full" allow-create filterable>
              <el-option v-for="item in EDUCATION_LEVEL_OPTIONS" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="毕业年份" prop="graduationYear">
            <el-input-number v-model="form.graduationYear" :min="2000" :max="2100" class="w-full" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="生源地" prop="nativePlace">
            <el-input v-model="form.nativePlace" />
          </el-form-item>
        </el-col>
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
      </el-row>
      <div class="form-actions">
        <el-button @click="loadProfile">重新加载</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          {{ exists ? '保存更新' : '创建档案' }}
        </el-button>
      </div>
    </el-form>
  </PageSection>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import PageSection from '../../components/common/PageSection.vue'
import { createCurrentStudentProfile, getCurrentStudentProfile, updateCurrentStudentProfile } from '../../api/student'
import { EDUCATION_LEVEL_OPTIONS, EMPLOYMENT_STATUS_OPTIONS, GENDER_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const formRef = ref()
const exists = ref(false)
const submitting = ref(false)
const form = reactive({
  studentNo: '',
  gender: 1,
  collegeName: '',
  majorName: '',
  className: '',
  educationLevel: '本科',
  graduationYear: new Date().getFullYear(),
  nativePlace: '',
  employmentStatus: 1
})

const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  collegeName: [{ required: true, message: '请输入学院', trigger: 'blur' }],
  majorName: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  educationLevel: [{ required: true, message: '请选择学历层次', trigger: 'change' }],
  graduationYear: [{ required: true, message: '请输入毕业年份', trigger: 'change' }],
  employmentStatus: [{ required: true, message: '请选择就业状态', trigger: 'change' }]
}

function fillForm(data) {
  Object.assign(form, {
    studentNo: data.studentNo || '',
    gender: data.gender || 1,
    collegeName: data.collegeName || '',
    majorName: data.majorName || '',
    className: data.className || '',
    educationLevel: data.educationLevel || '本科',
    graduationYear: data.graduationYear || new Date().getFullYear(),
    nativePlace: data.nativePlace || '',
    employmentStatus: data.employmentStatus ?? 1
  })
}

async function loadProfile() {
  try {
    const data = await getCurrentStudentProfile()
    fillForm(data)
    exists.value = true
  } catch {
    exists.value = false
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)

  if (!valid) {
    return
  }

  submitting.value = true

  try {
    const action = exists.value ? updateCurrentStudentProfile : createCurrentStudentProfile
    const data = await action({ ...form })
    fillForm(data)
    const wasExisting = exists.value
    exists.value = true
    notifySuccess(wasExisting ? '学生档案已保存' : '学生档案已创建')
  } finally {
    submitting.value = false
  }
}

onMounted(loadProfile)
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
