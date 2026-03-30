<template>
  <PageSection title="学生管理" description="查看、编辑和禁用学生档案。">
    <el-table :data="students" border>
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="collegeName" label="学院" width="160" />
      <el-table-column prop="majorName" label="专业" width="160" />
      <el-table-column prop="educationLevel" label="学历" width="100" />
      <el-table-column prop="employmentStatusName" label="就业状态" width="140" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="disableStudent(row.id)">禁用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="编辑学生档案" width="720px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="学号"><el-input v-model="form.studentNo" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="学院"><el-input v-model="form.collegeName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="专业"><el-input v-model="form.majorName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="班级"><el-input v-model="form.className" /></el-form-item></el-col>
          <el-col :span="12">
            <el-form-item label="学历">
              <el-select v-model="form.educationLevel" class="w-full">
                <el-option v-for="item in EDUCATION_LEVEL_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="就业状态">
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
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import { deleteAdminStudent, listAdminStudents, updateAdminStudent } from '../../api/student'
import { EDUCATION_LEVEL_OPTIONS, EMPLOYMENT_STATUS_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const students = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
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

async function loadData() {
  students.value = await listAdminStudents()
}

function openDialog(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function submitForm() {
  const payload = { ...form }
  delete payload.id
  await updateAdminStudent(form.id, payload)
  dialogVisible.value = false
  students.value = await listAdminStudents()
  notifySuccess('学生档案已更新')
}

async function disableStudent(id) {
  await ElMessageBox.confirm('确认禁用该学生档案吗？', '提示', { type: 'warning' })
  await deleteAdminStudent(id)
  students.value = await listAdminStudents()
  notifySuccess('学生档案已禁用')
}

onMounted(loadData)
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
