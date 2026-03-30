<template>
  <PageSection
    title="我的投递"
    description="查看当前学生已投递的岗位记录。"
    :loading="loading"
    :empty="!applications.length"
    empty-description="当前还没有投递记录"
  >
    <el-table :data="applications" border empty-text="当前还没有投递记录">
      <el-table-column prop="jobTitle" label="岗位名称" min-width="180" />
      <el-table-column prop="enterpriseName" label="企业名称" min-width="180" />
      <el-table-column prop="resumeName" label="简历名称" min-width="160" />
      <el-table-column label="投递状态" width="140">
        <template #default="{ row }">
          <StatusTag :text="row.applicationStatusName" :status="row.applicationStatus" mode="application" />
        </template>
      </el-table-column>
      <el-table-column label="投递时间" width="180">
        <template #default="{ row }">{{ formatDateTime(row.applyTime) }}</template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" min-width="180" />
    </el-table>
  </PageSection>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import PageSection from '../../components/common/PageSection.vue'
import StatusTag from '../../components/common/StatusTag.vue'
import { listMyJobApplications } from '../../api/jobApplication'
import { formatDateTime } from '../../utils/format'

const applications = ref([])
const loading = ref(false)

async function loadData() {
  loading.value = true

  try {
    applications.value = await listMyJobApplications()
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>
