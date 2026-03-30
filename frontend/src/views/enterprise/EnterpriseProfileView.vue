<template>
  <div class="page-grid">
    <PageSection title="企业档案" description="维护当前企业主体档案。">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="企业名称" prop="enterpriseName">
              <el-input v-model="form.enterpriseName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="统一信用码" prop="creditCode">
              <el-input v-model="form.creditCode" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业">
              <el-input v-model="form.industryName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业规模">
              <el-select v-model="form.enterpriseScale" class="w-full" allow-create filterable>
                <el-option v-for="item in ENTERPRISE_SCALE_OPTIONS" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱">
              <el-input v-model="form.contactEmail" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业地址">
              <el-input v-model="form.address" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="企业简介">
              <el-input v-model="form.introduction" type="textarea" :rows="4" />
            </el-form-item>
          </el-col>
        </el-row>
        <div class="form-actions">
          <el-button @click="loadProfile">重新加载</el-button>
          <el-button type="primary" @click="submitProfile">{{ exists ? '保存更新' : '创建档案' }}</el-button>
        </div>
      </el-form>
    </PageSection>

    <PageSection title="审核状态" description="当前企业审核结果。">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="审核状态">{{ reviewStatus?.reviewStatusName || profile?.reviewStatusName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核备注">{{ reviewStatus?.reviewRemark || profile?.reviewRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </PageSection>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import PageSection from '../../components/common/PageSection.vue'
import {
  createCurrentEnterpriseProfile,
  getCurrentEnterpriseProfile,
  getCurrentEnterpriseReviewStatus,
  updateCurrentEnterpriseProfile
} from '../../api/enterprise'
import { ENTERPRISE_SCALE_OPTIONS } from '../../constants/options'
import { notifySuccess } from '../../utils/message'

const formRef = ref()
const exists = ref(false)
const profile = ref(null)
const reviewStatus = ref(null)
const form = reactive({
  enterpriseName: '',
  creditCode: '',
  industryName: '',
  enterpriseScale: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  introduction: ''
})

const rules = {
  enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  creditCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

function fillForm(data = {}) {
  Object.assign(form, {
    enterpriseName: data.enterpriseName || '',
    creditCode: data.creditCode || '',
    industryName: data.industryName || '',
    enterpriseScale: data.enterpriseScale || '',
    contactName: data.contactName || '',
    contactPhone: data.contactPhone || '',
    contactEmail: data.contactEmail || '',
    address: data.address || '',
    introduction: data.introduction || ''
  })
}

async function loadProfile() {
  try {
    profile.value = await getCurrentEnterpriseProfile()
    fillForm(profile.value)
    reviewStatus.value = await getCurrentEnterpriseReviewStatus()
    exists.value = true
  } catch {
    exists.value = false
  }
}

async function submitProfile() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  const action = exists.value ? updateCurrentEnterpriseProfile : createCurrentEnterpriseProfile
  profile.value = await action({ ...form })
  exists.value = true
  reviewStatus.value = await getCurrentEnterpriseReviewStatus().catch(() => null)
  notifySuccess('企业档案已保存')
}

onMounted(loadProfile)
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
