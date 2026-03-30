<template>
  <PageSection title="企业管理" description="查看企业档案、编辑基础信息并执行审核。">
    <el-table :data="enterprises" border>
      <el-table-column prop="enterpriseName" label="企业名称" min-width="220" />
      <el-table-column prop="industryName" label="所属行业" width="140" />
      <el-table-column prop="contactName" label="联系人" width="120" />
      <el-table-column prop="contactPhone" label="联系电话" width="140" />
      <el-table-column prop="reviewStatusName" label="审核状态" width="120" />
      <el-table-column label="操作" width="240">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="success" @click="openReviewDialog(row)">审核</el-button>
          <el-button v-if="isAdmin" link type="danger" @click="disableEnterprise(row.id)">禁用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editDialogVisible" title="编辑企业档案" width="720px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="企业名称"><el-input v-model="form.enterpriseName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="统一信用码"><el-input v-model="form.creditCode" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="所属行业"><el-input v-model="form.industryName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="企业规模"><el-input v-model="form.enterpriseScale" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系电话"><el-input v-model="form.contactPhone" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="联系邮箱"><el-input v-model="form.contactEmail" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="企业地址"><el-input v-model="form.address" /></el-form-item></el-col>
          <el-col :span="24"><el-form-item label="企业简介"><el-input v-model="form.introduction" type="textarea" :rows="4" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reviewDialogVisible" title="审核企业档案" width="520px">
      <el-form :model="reviewForm" label-width="90px">
        <el-form-item label="审核状态">
          <el-select v-model="reviewForm.reviewStatus" class="w-full">
            <el-option v-for="item in REVIEW_STATUS_OPTIONS.slice(1)" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="reviewForm.reviewRemark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交审核</el-button>
      </template>
    </el-dialog>
  </PageSection>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessageBox } from 'element-plus'
import PageSection from '../../components/common/PageSection.vue'
import { deleteAdminEnterprise, listAdminEnterprises, reviewEnterprise, updateAdminEnterprise } from '../../api/enterprise'
import { REVIEW_STATUS_OPTIONS } from '../../constants/options'
import { useIdentityStore } from '../../utils/identity'
import { ROLE_CODES } from '../../constants/identity'
import { notifySuccess } from '../../utils/message'

const { currentIdentity } = useIdentityStore()
const isAdmin = computed(() => currentIdentity.value.roleCode === ROLE_CODES.ADMIN)

const enterprises = ref([])
const editDialogVisible = ref(false)
const reviewDialogVisible = ref(false)
const form = reactive({
  id: null,
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
const reviewForm = reactive({
  id: null,
  reviewStatus: 1,
  reviewRemark: ''
})

async function loadData() {
  enterprises.value = await listAdminEnterprises()
}

function openEditDialog(row) {
  Object.assign(form, row)
  editDialogVisible.value = true
}

function openReviewDialog(row) {
  reviewForm.id = row.id
  reviewForm.reviewStatus = row.reviewStatus === 2 ? 2 : 1
  reviewForm.reviewRemark = row.reviewRemark || ''
  reviewDialogVisible.value = true
}

async function submitEdit() {
  const payload = { ...form }
  delete payload.id
  await updateAdminEnterprise(form.id, payload)
  editDialogVisible.value = false
  enterprises.value = await listAdminEnterprises()
  notifySuccess('企业档案已更新')
}

async function submitReview() {
  await reviewEnterprise(reviewForm.id, {
    reviewStatus: reviewForm.reviewStatus,
    reviewRemark: reviewForm.reviewRemark
  })
  reviewDialogVisible.value = false
  enterprises.value = await listAdminEnterprises()
  notifySuccess('企业审核已完成')
}

async function disableEnterprise(id) {
  await ElMessageBox.confirm('确认禁用该企业档案吗？', '提示', { type: 'warning' })
  await deleteAdminEnterprise(id)
  enterprises.value = await listAdminEnterprises()
  notifySuccess('企业档案已禁用')
}

onMounted(loadData)
</script>

<style scoped>
.w-full {
  width: 100%;
}
</style>
